package bea;

import java.io.*;
import java.util.Vector;
import weblogic.utils.collections.NumericHashtable;
import weblogic.utils.collections.NumericValueHashtable;
import weblogic.wtc.jatmi.FldTbl;

public class DynamicFldTbl implements FldTbl
{
	private static final int FNMASK32 = 0x1ffffff;
	private static final int FTMASK32 = 63;
	private static final int FTSHIFT32 = 25;
	private static final int FNMASK = 8191;
	private static final int FTMASK = 7;
	private static final int FTSHIFT = 13;
	private NumericValueHashtable nameToFieldHashtable;
	private NumericHashtable fieldToNameHashtable;
	private Vector fieldNameSet;
	private int currSpot;
	private int lineNo;
	private int inputStringLength;
	private char inputChars[];


	public DynamicFldTbl(String filename, boolean flag) {
		this(filename, flag, 101, 0.75F);
	}

	public DynamicFldTbl(String filename, boolean flag, int i, float f) {
		lineNo = 0;
		BufferedReader bufferedreader = null;
		int base_fml = 0;
		File file = new File(filename);
		try {
			bufferedreader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException filenotfoundexception) {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			java.io.InputStream inputstream = classloader.getResourceAsStream(filename);

			if (bufferedreader != null) {
				try {
					bufferedreader.close();
				} catch (Throwable ignore) {
					ignore.printStackTrace();
				}
			}

			if (inputstream == null) {
				System.out.println("No se ha podido leer el fichero de definicion de buffers.");
				return;
			}

			bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
		}

		NumericValueHashtable numericvaluehashtable = new NumericValueHashtable(i, f);
		NumericHashtable numerichashtable = new NumericHashtable(i, f);
		Vector vector = new Vector();

		String linea;

		try {

			do {

				linea = "";

				try {
					if ((linea = bufferedreader.readLine()) == null)
						break;
				} catch (IOException ioexception) {
					break;
				}

				linea = linea.trim();
				lineNo++;
				currSpot = 0;
				inputStringLength = linea.length();

				if (inputStringLength != 0 && !linea.startsWith("#")
						&& !linea.startsWith("$") && !linea.startsWith("END")) {
					inputChars = linea.toCharArray();

					if (linea.startsWith("*base")) {
						if (inputStringLength < 7 || !Character.isWhitespace(linea.charAt(5)))
							return;
						currSpot = 6;
						if (!skipWhiteSpace())
							return;
						if (!Character.isDigit(linea.charAt(currSpot)))
							return;
						base_fml = getInteger();
					} else {
						String nombre_buffer = getString();
						if (!skipWhiteSpace())
							return;
						if (!Character.isDigit(inputChars[currSpot]))
							return;
						int identificador_fml = getInteger() + base_fml;
						if (flag) {
							if (identificador_fml > FNMASK32) {
								return;
							}
						} else if (identificador_fml > FNMASK) {
							return;
						}
						if (!skipWhiteSpace())
							return;
						String tipo_buffer = getString();
						byte byte0;

						if (tipo_buffer.equals("char"))
							byte0 = 2;
						else if (tipo_buffer.equals("string"))
							byte0 = 5;
						else if (tipo_buffer.equals("short"))
							byte0 = 0;
						else if (tipo_buffer.equals("long"))
							byte0 = 1;
						else if (tipo_buffer.equals("float"))
							byte0 = 3;
						else if (tipo_buffer.equals("double"))
							byte0 = 4;
						else if (tipo_buffer.equals("carray"))
							byte0 = 6;
						else if (flag && tipo_buffer.equals("ptr"))
							byte0 = 9;
						else if (flag && tipo_buffer.equals("fml32"))
							byte0 = 10;
						else if (flag && tipo_buffer.equals("view32"))
							byte0 = 11;
						else
							return;
						int l;

						if (flag)
							l = (byte0 & FTMASK32) << FTSHIFT32 | identificador_fml & FNMASK32;
						else
							l = (byte0 & FTMASK) << FTSHIFT | identificador_fml & FNMASK;
						numerichashtable.put(l, nombre_buffer);
						numericvaluehashtable.put(nombre_buffer, l);
						vector.add(nombre_buffer);
					}
				}
			} while (true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedreader != null) {
				try {
					bufferedreader.close();
				} catch (Throwable ignore) {
					ignore.printStackTrace();
				}
			}
		}

		file = null;
		nameToFieldHashtable = numericvaluehashtable;
		fieldToNameHashtable = numerichashtable;
		fieldNameSet = vector;
	}

	private int getInteger() {
		int i = currSpot;
		for (; currSpot < inputStringLength
				&& Character.isDigit(inputChars[currSpot]); currSpot++)
			;
		int j = (new Integer(new String(inputChars, i, currSpot - i)))
				.intValue();
		return j;
	}

	private String getString() {
		int i = currSpot;
		for (; currSpot < inputStringLength
				&& !Character.isWhitespace(inputChars[currSpot]); currSpot++)
			;
		String s = new String(inputChars, i, currSpot - i);
		return s;
	}

	private boolean skipWhiteSpace() {
		for (; currSpot < inputStringLength
				&& Character.isWhitespace(inputChars[currSpot]); currSpot++)
			;
		return currSpot < inputStringLength;
	}

	public String Fldid_to_name(int i) {
		if (fieldToNameHashtable == null)
			return null;
		else
			return (String) fieldToNameHashtable.get(i);
	}

	public String[] getFldNames() {
		if (fieldNameSet == null) {
			return new String[0];
		} else {
			String as[] = new String[fieldNameSet.size()];
			as = (String[]) fieldNameSet.toArray(as);
			return as;
		}
	}

	public int name_to_Fldid(String s) {
		if (nameToFieldHashtable == null)
			return -1;
		int i = (int) nameToFieldHashtable.get(s);
		if (i == 0)
			return -1;
		else
			return i;
	}
}
