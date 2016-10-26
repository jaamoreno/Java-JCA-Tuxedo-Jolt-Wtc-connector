package bea.jolt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class JoltDefinition {

	public JoltDefinition(String s, String s1) {
		d_prop = new Hashtable(7);
		d_params = new Hashtable(13);
		d_defName = s;
		d_typeName = s1;
	}

	public JoltDefinition(String s) throws DefinitionException {
		// se crea el hastable de propiedades
		d_prop = new Hashtable(7);
		// se crea el hastable de parametros
		d_params = new Hashtable(13);

		// se parsea la cadena
		parse(s);
	}

	public JoltDefinition()
	// (WAS-REDSYS)
	throws DefinitionException
	{
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return d_defName;
	}

	public int getVersion() {
		return d_version;
	}

	public String getTypeName() {
		return d_typeName;
	}

	public int getType() {
		String s = d_typeName.substring(0, d_typeName.indexOf('/'));
		if ("SVC".equals(s))
			return 7;
		return !"QUEUE".equals(s) ? 0 : 8;
	}

	public int numParams() {
		return d_params.size();
	}

	public Enumeration getParams() {
		return new ParamEnumeration(d_firstParam);
	}

	String id2name(int i) {
		for (JoltParam joltparam = d_firstParam; joltparam != null; joltparam = joltparam.p_nextParam)
			if (joltparam.p_fid == i)
				return joltparam.p_name;

		return null;
	}

	void name2p(String s, int i, int j, JoltParam ajoltparam[])
			throws NoSuchFieldError, IllegalAccessError,
			IllegalArgumentException {
		int k = 0;
		JoltParam joltparam;
		do {
			joltparam = getParam(s);
			if (k == 0) {
				if ((joltparam.p_access & i) != i)
					throw new IllegalAccessError(s);
				ajoltparam[0] = joltparam;
			}
		} while ((s = joltparam.p_fname) != null && ++k < 2);
		if (SBuffer.TYPE(joltparam.p_fid) != j) {
			throw new IllegalArgumentException(joltparam.p_name);
		} else {
			ajoltparam[1] = joltparam;
			return;
		}
	}

	void name2p(String s, int i, JoltParam ajoltparam[])
			throws NoSuchFieldError, IllegalArgumentException {
		int j = 0;
		JoltParam joltparam;
		do {
			joltparam = getParam(s);
			if (j == 0) {
				if ((joltparam.p_access & i) != i)
					throw new IllegalAccessError(s);
				ajoltparam[0] = joltparam;
			}
		} while ((s = joltparam.p_fname) != null && ++j < 2);
		ajoltparam[1] = joltparam;
	}

	public JoltParam getParam(String s) throws NoSuchFieldError {
		if (s == null || d_params == null)
			throw new NoSuchFieldError(s);
		JoltParam joltparam;
		if ((joltparam = (JoltParam) d_params.get(s)) == null)
			throw new NoSuchFieldError(s);
		else
			return joltparam;
	}

	public String getProperty(String s) {
		return (String) d_prop.get(s);
	}

	public void putProperty(String s, String s1) {
		d_prop.put(s, s1);
	}

	public Enumeration getKeys() {
		return d_prop.keys();
	}

	public String toString() {
		StringBuffer stringbuffer = (new StringBuffer(d_typeName)).append(':');
		for (Enumeration enumeration = d_prop.keys(); enumeration
				.hasMoreElements(); stringbuffer.append(':')) {
			String s1 = (String) enumeration.nextElement();
			stringbuffer.append(s1);
			String s;
			if ((s = (String) d_prop.get(s1)) != null)
				stringbuffer.append('=').append(s);
		}

		for (JoltParam joltparam = d_firstParam; joltparam != null; joltparam = joltparam.p_nextParam)
			stringbuffer.append(joltparam.toString());

		return stringbuffer.toString();
	}

	public JoltParam addParam(String s, JoltParam joltparam,
			JoltParam joltparam1) {
		joltparam.p_nextParam = null;
		d_params.put(s, joltparam);
		if (joltparam1 == null)
			d_firstParam = joltparam;
		else
			joltparam1.p_nextParam = joltparam;
		return joltparam;
	}

	private void parse(String s) throws DefinitionException {
		JoltParam joltparam = null;
		JoltParam joltparam2 = null;

		// se parte la cadena en tokens separados por ":"
		StringTokenizer stringtokenizer = new StringTokenizer(s, ":", false);

		if (!stringtokenizer.hasMoreTokens())
			throw new DefinitionException("Invalid " + s);

		d_typeName = stringtokenizer.nextToken(); // SVC/TOUPPER
		d_defName = d_typeName.substring(d_typeName.indexOf('/') + 1); // TOUPPER
		d_firstParam = null;
		int i = 1;

		// recorrido de tokens
		// ejemplo
		// vs=1:ex=1:bt=FML:\
		// bp:pn=ACCOUNT_ID:pt=integer:pf=33554436:pa=wr:ep:\
		// bp:pn=SBALANCE:pt=string:pf=167772164:pa=rd:ep:\
		// bp:pn=STATLIN:pt=string:pf=167772163:pa=rd:ep:\
		// bp:pn=FORMNAM:pt=string:pf=167772165:pa=rd:ep:
		//
		// donde pn es param_name
		// pt es param_type
		// pf es param_field_id
		// pa es param_access
		//
		// pueden ser propiedades o parametros
		// los tokens ademas pueden tener o no un igual entre la propiedad y el
		// valor
		// concretamente la marca de inicio y fin de parametro no lo llevan
		//
		while (stringtokenizer.hasMoreTokens()) {
			i++;
			String token = stringtokenizer.nextToken();
			String right;
			String left_or_full;
			int j;

			if ((j = token.indexOf('=')) < 0) {
				// si no hay igual en el token
				left_or_full = token;
				right = null;
			} else {
				// si hay igual en el token
				left_or_full = token.substring(0, j);
				right = token.substring(j + 1);
			}

			try {
				String aux;
				if ((aux = (String) d_hash.get(left_or_full)) != null) {
					// inicio de parametro
					if (aux.hashCode() == "bp".hashCode()) {
						joltparam2 = new JoltParam();
						continue; // ir al siguiente token
					}

					// fin de parametro
					if (aux.hashCode() == "ep".hashCode()) {
						if (joltparam2 == null)
							throw new DefinitionException(
									"Invalid attribute - " + "Attribute Name :"
											+ left_or_full + TOKENPOS + i);
						if ((aux = joltparam2.getName()) == null)
							throw new DefinitionException(
									"No Parameter(pn) before end of parameter(ep) "
											+ TOKENPOS + i);

						// se anhade el parametro finalizado al hastable de
						// parametros
						// si es el primer parametro se guarda como primer
						// parametro
						// ademas se actualiza la referencia al siguiente
						// parametro del ultimo parametro (joltparam)
						// devuelve el nuevo parametro como ultimo parametro
						joltparam = addParam(aux, joltparam2, joltparam);
						joltparam2 = null;
						continue;
					}

					// version
					if (aux.hashCode() == "vs".hashCode())
						d_version = JoltDefAttrs.getInteger(right);
				}

				// si al llegar aqui el joltparam2 es null significa que el
				// token actual es una propiedad del servicio
				// como bt (buffer type) ya que no se ha encontrado el token
				// inicio de parametro
				// y se mete en el hastable de propiedades
				// si en cambio joltparam2 no es null significa que hemos
				// encontrado un token de inicio de parametro
				// con anterioridad y el token actual es una propiedad del
				// parametro
				// y se anhade la propiedad a joltparam2
				if (joltparam2 == null) {
					d_prop.put(left_or_full, right);
				} else {
					joltparam2.addProperty(left_or_full, right);
				}
			} catch (NumberFormatException _ex) {
				throw new DefinitionException(token + TOKENPOS + i);
			} catch (NullPointerException _ex) {
				throw new DefinitionException(token + TOKENPOS + i);
			}
		}

		// recorrido de los parametros
		for (JoltParam joltparam1 = d_firstParam; joltparam1 != null; joltparam1 = joltparam1.p_nextParam) {
			JoltParam joltparam3;
			try {
				// todos los parametros iguales deben tener el mismo
				// identificador y para ello se pone el identificador
				// del primero
				if (joltparam1.p_fname != null
						&& (joltparam3 = getParam(joltparam1.p_fname)) != null)
					joltparam1.p_fid = joltparam3.p_fid;
			} catch (NoSuchFieldError _ex) {
				throw new DefinitionException(joltparam1.p_fname
						+ " does not exist, but specified in "
						+ joltparam1.p_name);
			}
		}

	}

	private String d_typeName;

	private String d_defName;

	private Hashtable d_prop;

	private int d_version;

	private JoltParam d_firstParam;

	private Hashtable d_params;

	private static Hashtable d_hash;

	private static String TOKENPOS = " at token ";

	static {
		d_hash = new Hashtable(5);
		d_hash.put("vs", "vs");
		d_hash.put("bp", "bp");
		d_hash.put("ep", "ep");
	}
}
