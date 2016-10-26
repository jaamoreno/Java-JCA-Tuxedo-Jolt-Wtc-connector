package bea.jolt.pool.servlet;

import bea.jolt.pool.Result;

public class ServletResult extends Result {

	private static final long serialVersionUID = 1L;

	ServletResult(int i) {
		super(i);
	}

	public String getStringValue(String s, int i, String s1) {
		Object obj = getValue(s, i, s1);
		if (obj == null)
			return null;
		if (obj instanceof byte[]) {
			byte abyte0[] = (byte[]) obj;
			StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
			for (int j = 0; j < abyte0.length; j++) {
				stringbuffer.append(Hex[abyte0[j] >>> 4 & 0xf]);
				stringbuffer.append(Hex[abyte0[j] & 0xf]);
			}

			return stringbuffer.toString();
		} else {
			return obj.toString();
		}
	}

	public String getStringValue(String s, String s1) {
		return getStringValue(s, 0, s1);
	}

	private static char Hex[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'A', 'B', 'C', 'D', 'E', 'F' };

}