package bea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

public abstract class JoltRepositoryLoader {

	public static Properties loadProperties(String name, ClassLoader loader, Tracer tracer)
			throws IllegalArgumentException {

		Properties result = null;

		if (name == null) {
			throw new IllegalArgumentException("null input: name");
		}

		if (name.startsWith("/")) {
			name = name.substring(1);
		}

		if (name.endsWith(SUFFIX)) {
			name = name.substring(0, name.length() - SUFFIX.length());
		}

		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			if (loader == null) {
				loader = ClassLoader.getSystemClassLoader();
			}

			name = name.replace('.', '/');

			if (!name.endsWith(SUFFIX)) {
				name = name.concat(SUFFIX);
			}

			inputStream = loader.getResourceAsStream(name);

			if (inputStream != null) {
				result = new Properties();

				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

				String linea = "";
				int inputStringLength = 0;
				boolean concat = false; // indica si hay que concatenar
				boolean first = true; // indica si es el primer servicio
				String service_line = "";
				Vector servicios = new Vector();
				int numServicios = 0;
				String service = "";
				String definition = "";

				while((linea = bufferedReader.readLine()) != null){

					inputStringLength = linea.length();

					if (inputStringLength != 0 && !linea.startsWith("#") && !linea.startsWith("!")) {

						if (linea.startsWith("add PKG")) {
							// no se debe concatenar nada hasta encontrar un add SVC
							concat = false;
						}
						else if (linea.startsWith("add SVC") || linea.startsWith("addSVC")) {

							// se debe concatenar
							concat = true;

							if (first == true) {
								// para el primer servicio
								first = false;
							} else {
								// no es el primer servicio
								// por lo tanto se tiene uno completo
								// meter en el vector de servicios
								numServicios ++;
								servicios.addElement(service_line);
							}

							service_line = "";

							// eliminar la contrabarra de continuacion de linea
							if (linea.endsWith("\\")) {
								linea = linea.substring(0, linea.length()-1);
							}
							service_line += linea;

						} else {
							if (concat ==  true) {

								// eliminar la contrabarra de continuacion de linea
								if (linea.endsWith("\\")) {
									linea = linea.substring(0, linea.length()-1);
								}
								service_line += linea;
							}
						}
					}
				} // fin while((linea = bufferedReader.readLine()) != null){

				// si al acabar de leer service_line empieza por add SVC
				// es que tiene la definicion del ultimo servicio
				// hay que meterla en el vector de servicios
				if (service_line.startsWith("add SVC") || service_line.startsWith("addSVC")) {
					numServicios ++;
					servicios.addElement(service_line);
				}

				tracer.traceln("Total de servicios es  [" + numServicios + "]", Tracer.TRACE_PET_LEVEL);
				tracer.traceln("Total de servicios es  [" + servicios.size() + "]", Tracer.TRACE_PET_LEVEL);
				tracer.traceln("-------BEGIN-----------------", Tracer.TRACE_PET_LEVEL);

				for (int i = 0; i < servicios.size(); i++) {
					linea = (String)servicios.get(i);

					// quitar "add SVC/"
					if (linea.startsWith("add SVC/")) {
						linea = linea.substring(8);
					} else if (linea.startsWith("addSVC/")) {
						linea = linea.substring(7);
					}

					// poner "addSVC/ para no cambiar SessionPool
					service = "addSVC/";

					// nombre servicio parte izquierda dos puntos
					service += linea.substring(0, linea.indexOf(':'));

					// definicion servicio parte derecha dos puntos (sin los dos puntos)
					definition = linea.substring(linea.indexOf(':') + 1);

					tracer.traceln("service es [" + service + "]", Tracer.TRACE_PET_LEVEL);
					tracer.traceln("definition es [" + definition + "]", Tracer.TRACE_PET_LEVEL);

					// meter en properties
					result.setProperty(service, definition);
				}
				tracer.traceln("-------END-------------------", Tracer.TRACE_PET_LEVEL);
				tracer.traceln("Total de servicios properties es  [" + result.size() + "]", Tracer.TRACE_PET_LEVEL);
			}
		} catch (Exception e) {
			result = null;
			e.printStackTrace();
		} finally {

			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Throwable ignore) {
					ignore.printStackTrace();
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable ignore) {
					ignore.printStackTrace();
				}
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("could not load [" + name + "]"
					+ " as " + "a classloader resource");
		} else {
			return result;
		}
	}

	public static Properties loadProperties(String name, Tracer tracer)
			throws IllegalArgumentException {
		return loadProperties(name, Thread.currentThread().getContextClassLoader(), tracer);
	}

	private JoltRepositoryLoader() {
	}

	private static final String SUFFIX = ".properties";
}
