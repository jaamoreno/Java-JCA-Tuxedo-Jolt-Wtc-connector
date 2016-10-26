package bea.jolt.pool.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import bea.jolt.pool.DataSet;

public class ServletDataSet extends DataSet {

	private static final long serialVersionUID = 1L;

	public ServletDataSet() {
	}

	public ServletDataSet(int i) {
		super(i);
	}

	public void importRequest(HttpServletRequest httpservletrequest) {
		int ai[] = new int[1];
		for (Enumeration enumeration = httpservletrequest.getParameterNames(); enumeration
				.hasMoreElements();) {
			String s1 = (String) enumeration.nextElement();
			String s = stripIndex(s1, ai);
			if (super.unrestricted || containsKey(s)) {
				String as[] = httpservletrequest.getParameterValues(s1);
				if (as != null) {
					if (as.length == 1) {
						setValue(s, ai[0], as[0]);
					} else {
						for (int i = as.length; --i >= 0;)
							setValue(s, i, as[i]);
					}
				}
			}
		}
	}

	protected ServletDataSet(int i, boolean flag) {
		super(i, flag);
	}

	private static String stripIndex(String s, int ai[]) {
		int i;
		if ((i = s.lastIndexOf("_")) > 0 && Character.isDigit(s.charAt(i + 1)))
			try {
				ai[0] = Integer.parseInt(s.substring(i + 1));
				return s.substring(0, i);
			} catch (NumberFormatException _ex) {
			}
		ai[0] = 0;
		return s;
	}
}