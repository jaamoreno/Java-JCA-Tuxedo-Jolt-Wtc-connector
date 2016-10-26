package bea.jolt.pool;

import bea.DynamicFldTbl;
import weblogic.wtc.jatmi.Ferror;
import weblogic.wtc.jatmi.FmlKey;
import weblogic.wtc.jatmi.TypedFML32;
import bea.JoltRepositoryLoader;
import bea.PropertyLoader;
import bea.Tracer;
import bea.jolt.*;
import java.util.*;

public class SessionPool extends Factory
{
	private String fmlFileNames;
	private DynamicFldTbl fieldTables[];
	private Factory factory;
	private JoltSession joltSession;
	private Properties joltprop;
	private Properties fldprop;
	private UserInfo s_usr;
	private Tracer tracer;
	private Date creationDate;
	boolean updatingConf;
	private int refreshConfTimeOut;


	protected SessionPool(String as[], String as1[], int i, int j,
			UserInfo userinfo, boolean flag) {
		joltprop = null;
		fldprop = null;
		tracer = null;
		joltSession = new JoltSession();
		s_usr = userinfo;
		factory = this;


		System.out.println("Inicio bea.jolt.pool.SessionPool");
		// cargar el fichero de properties
		fldprop = PropertyLoader.loadProperties("gfijoltwtc");

		// obtener variables del properties para el trazador
		String dirlog = fldprop.getProperty("dir_fich_log", "default");
		String nomlog = fldprop.getProperty("nom_fich_log", "default");
		int tracelevel = (new Integer(fldprop.getProperty("ntrazas", "0"))).intValue();

		// crear el trazador
		tracer = new Tracer(dirlog, nomlog);
		tracer.setLogLevel(tracelevel);

		// cargar jrepository
		joltprop = JoltRepositoryLoader.loadProperties("jrepository", tracer);
		//joltprop = PropertyLoader.loadProperties("jrepository");

		// cargar los fieldtables genericos
		fmlFileNames = fldprop.getProperty("fldtables", "default");
		setFieldTables(loadFileBuffers(fmlFileNames));

		refreshConfTimeOut = (new Integer(fldprop.getProperty("refresh_conf_timeout", "3600"))).intValue();

		// nueva fecha de refresco
		creationDate = new Date();
		updateConf(false);
		System.out.println("Fin bea.jolt.pool.SessionPool");
	}

	public SessionPool() {
		joltprop = null;
		fldprop = null;
		tracer = null;
		joltSession = new JoltSession();
		factory = this;

		// cargar el fichero de properties
		fldprop = PropertyLoader.loadProperties("gfijoltwtc");

		// obtener variables del properties para el trazador
		String dirlog = fldprop.getProperty("dir_fich_log", "default");
		String nomlog = fldprop.getProperty("nom_fich_log", "default");
		int tracelevel = (new Integer(fldprop.getProperty("ntrazas", "0"))).intValue();

		// crear el trazador
		tracer = new Tracer(dirlog, nomlog);
		tracer.setLogLevel(tracelevel);

		// cargar jrepository
		joltprop = JoltRepositoryLoader.loadProperties("jrepository", tracer);
		//joltprop = PropertyLoader.loadProperties("jrepository");

		// cargar los fieldtables genericos
		fmlFileNames = fldprop.getProperty("fldtables", "default");
		setFieldTables(loadFileBuffers(fmlFileNames));

		refreshConfTimeOut = (new Integer(fldprop.getProperty("refresh_conf_timeout", "3600"))).intValue();

		// nueva fecha de refresco
		creationDate = new Date();
		updateConf(false);
	}

	protected Result call(String service, String as[], Transaction transaction)
			throws  bea.jolt.pool.SessionPoolException,
					bea.jolt.pool.ApplicationException,
					bea.jolt.pool.ServiceException,
					bea.jolt.pool.TransactionException {
		return privateCall(service, as, transaction);
	}

	public Result call(String service, DataSet dataset, Transaction transaction)
			throws  bea.jolt.pool.SessionPoolException,
					bea.jolt.pool.ApplicationException,
					bea.jolt.pool.ServiceException,
					bea.jolt.pool.TransactionException {
		return privateCall(service, dataset, transaction);
	}

	public Transaction startTransaction(int timeout)
			throws  bea.jolt.pool.SessionPoolException,
					bea.jolt.pool.TransactionException
	{
		System.out.println("bea.jolt.pool.SessionPool.startTransaction(1)");
		return new bea.jolt.pool.Transaction(timeout, joltSession);
	}

	public int setMaxConnections(int i) {
		return i;
	}

	public int getMaxConnections() {
		return 100;
	}

	public boolean isSuspended() {
		return false;
	}

	public Connection getConnection(int i) {
		return new Connection();
	}

	public String getPoolName() {
		return ".NONAME";
	}

	public String[] getAppAddr() {
		return null;
	}

	public String[] getFailOverAddr() {
		return null;
	}

	public int getMinConn() {
		return 0;
	}

	public UserInfo getUser() {
		return s_usr;
	}

	public boolean propagateSecurityContext() {
		return false;
	}

	public SecurityContext getSecurityContextClass() {
		return null;
	}

	private Result privateCall(String service, Object obj,
			Transaction transaction) {

		// si no se esta actualizando la configuracion y ya toca refrescarla
		if ((this.updatingConf == false) && (checkTimeOut(refreshConfTimeOut))) {
			updateConf(true);
		} else {
			updateConf(false);
		}

		JoltRemoteService jrs = new JoltRemoteService(service, joltSession, fldprop, tracer, fieldTables);

		Result result = factory.newResult(30);

		if (obj != null) {
			if (obj instanceof DataSet) {
				String name = "";
				Vector vector = null;
				for (Enumeration enumeration = ((DataSet) obj).keys(); enumeration
						.hasMoreElements();) {
					name = (String) enumeration.nextElement();
					vector = (Vector) ((DataSet) obj).get(name);
					int k = vector.size();
					for (int l = 0; l < k; l++)
						if (!name.equals("SVCNAME"))
							try {
								jrs.setField(name, l, vector.elementAt(l));
							} catch (Exception e) {
								jrs.done();
								e.printStackTrace();
								tracer.traceln("En SessionPool -> Servicio [" + service
										+ "] Error al construir buffer entrada [" + name + "]", Tracer.TRACE_PET_LEVEL);
								throw new bea.jolt.pool.ServiceException(e
										.getMessage()
										+ "[" + name + "]", e);
							}

				}

			} else if (obj instanceof String[]) {
				String as[] = (String[]) obj;
				for (int i = 0; i < as.length; i++) {
					if (as[i] != null) {
						String param[] = getNameVal(as[i]);
						if (param != null && !param[0].equals("SVCNAME")) {
							int fldid = 0;
							try {
								fldid = jrs.getRequest().Fldid(param[0]);
							} catch (Ferror e) {
								jrs.done();
								e.printStackTrace();
								tracer.traceln("En SessionPool -> Servicio [" + service
										+ "] Error al construir buffer entrada [" + param[0] + "]", Tracer.TRACE_PET_LEVEL);
								throw new bea.jolt.pool.ServiceException(e
										.getMessage()
										+ "[" + param[0] + "]", e);
							}
							switch (jrs.getRequest().Fldtype(fldid)) {
							default:
								break;

							case TypedFML32.FLD_DOUBLE:
								try {
									jrs.addDouble(param[0], Double.valueOf(
											param[1]).doubleValue());
									break;
								} catch (NumberFormatException e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_CHAR:
								try {
									jrs.addByte(param[0], (byte) param[1]
											.charAt(0));
									break;
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_CARRAY:
								try {
									jrs.addBytes(param[0], param[1].getBytes(),
											param[1].getBytes().length);
									break;
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_FLOAT:
								try {
									jrs.addFloat(param[0], Float.valueOf(
											param[1]).floatValue());
									break;
								} catch (NumberFormatException e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_LONG:
								try {
									jrs.addInt(param[0], Integer.valueOf(
											param[1]).intValue());
									break;
								} catch (NumberFormatException e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_SHORT:
								try {
									jrs.addShort(param[0], Short.valueOf(
											param[1]).shortValue());
									break;
								} catch (NumberFormatException e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}

							case TypedFML32.FLD_STRING:
								try {
									jrs.addString(param[0], param[1]);
									break;
								} catch (Exception e) {
									jrs.done();
									throw new bea.jolt.pool.ServiceException(e
											.getMessage(), e);
								}
							} // fin switch
						} // fin if (param != null &&
							// !param[0].equals("SVCNAME")) {
					} // fin if (as[i] != null) {
				} // fin for (int i = 0; i < as.length; i++) {
			} // fin } else if (obj instanceof String[]) {
		} // fin if (obj != null) {

		try {
			jrs.call(transaction);
			tracer.traceln("En SessionPool -> Servicio [" + service
					+ "] ejecutado correctamente.", Tracer.TRACE_INFO_LEVEL);
		} catch (bea.jolt.ApplicationException e) {
			tracer.traceln("En SessionPool -> Servicio [" + service
					+ "] Excepción ApplicationException.",
					Tracer.TRACE_INFO_LEVEL);
			e.printStackTrace();
			result.setApplicationCode(e.getApplicationCode());

			tracer.traceln("Comienza recorrido buffer", Tracer.TRACE_INFO_LEVEL);

			// Iterador para recorrido del buffer
			Iterator fmliter = jrs.getResponse().Fiterator();

			java.util.Map.Entry entry = null;
			FmlKey fmlkey = null;

			String fmlName = "";
			String fmlNameAux = "";
			int fldid = 0;
			int occurance = 0;
			int fldtype = 0;
			int contadora = 0;
			int contadorb = 0;
			int contadorc = 0;

			// Recorrido del buffer
			while (fmliter.hasNext()) {

				entry = (java.util.Map.Entry) fmliter.next();
				fmlkey = (FmlKey) entry.getKey();

				// identificador fml
				fldid = fmlkey.get_fldid();

				// Obtener nombre del buffer
				try {
					fmlName = jrs.getResponse().Fname(fldid);
				} catch (Ferror ee) {
					jrs.done();
					throw new bea.jolt.pool.ServiceException(ee.getMessage(), ee);
				}
				contadora++;

				if ((!fmlNameAux.equals("")) && (fmlName.equals(fmlNameAux))) {
					// Se salta
					contadorb++;
					continue;
				} else {
					// Se trata
					contadorc++;
					fmlNameAux = fmlName;
				}

				// obtener ocurrencias
				occurance = jrs.getResponse().Foccur(fldid);

				// Si hay ocurrencias
				if (occurance > 0) {

				// Crear el vector de ocurrencias
				Vector vector = new Vector(occurance);

				tracer.traceln("Buffer [" + fmlName + "] Ocurrencias [" + occurance + "]", Tracer.TRACE_PARAM_LEVEL);

				// tipo
				fldtype = jrs.getResponse().Fldtype(fldid);

				// Para cada ocurrencia
				for (int i = 0; i < occurance; i++) {

					// Dependiendo del tipo castear adecuadamente y meter en
					// vector de ocurrencias
					try {
						switch (fldtype) {
							case TypedFML32.FLD_DOUBLE: {
								vector.addElement((Double) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_CHAR: {
								vector.addElement((Character) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_CARRAY: {
								vector.addElement((byte[]) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_FLOAT: {
								vector.addElement((Float) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_LONG: {
								vector.addElement((Integer) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_SHORT: {
								vector.addElement((Short) jrs.getResponse().Fget(fldid, i));
								break;
							}
							case TypedFML32.FLD_STRING: {
								vector.addElement((String) jrs.getResponse().Fget(fldid, i));
								break;
							}
						} // fin switch
					} catch (Ferror ee) {
						jrs.done();
						throw new bea.jolt.pool.ServiceException(ee.getMessage(), ee);
					}
				} // fin for (int i = 0; i < occurance; i++) {

				// meter en result el vector de ocurrencias para el buffer
				result.put(fmlName, vector);

				} // fin if (occurance > 0) {
			} // fin while (fmliter.hasNext()) {

			tracer.traceln("Ocurrencias totales [" + contadora + "]", Tracer.TRACE_PARAM_LEVEL);
			tracer.traceln("Ocurrencias saltadas [" + contadorb + "]", Tracer.TRACE_PARAM_LEVEL);
			tracer.traceln("Ocurrencias tratadas [" + contadorc + "]", Tracer.TRACE_PARAM_LEVEL);

			tracer.traceln("Fin recorrido buffer", Tracer.TRACE_INFO_LEVEL);

			jrs.done();

			throw new bea.jolt.pool.ApplicationException(result, e.getMessage());
		} catch (bea.jolt.ServiceException e) {
			tracer
					.traceln("En SessionPool -> Servicio [" + service
							+ "] Excepción ServiceException.",
							Tracer.TRACE_INFO_LEVEL);
			e.printStackTrace();
			result.setApplicationCode(e.getErrno());
			jrs.done();
			throw new bea.jolt.pool.ServiceException(e.getMessage(), e);
		} catch (bea.jolt.TransactionException e) {
			tracer.traceln("En SessionPool -> Servicio [" + service
					+ "] Excepción TransactionException.",
					Tracer.TRACE_INFO_LEVEL);
			e.printStackTrace();
			result.setApplicationCode(e.getErrno());
			jrs.done();
			throw new bea.jolt.pool.TransactionException(e.getErrno(), e.getMessage(), e);
		}

		result.setApplicationCode(jrs.getApplicationCode());

		if (jrs.getResponse() != null) {

			tracer.traceln("Comienza recorrido buffer", Tracer.TRACE_INFO_LEVEL);

			// Iterador para recorrido del buffer
			Iterator fmliter = jrs.getResponse().Fiterator();

			java.util.Map.Entry entry = null;
			FmlKey fmlkey = null;

			String fmlName = "";
			String fmlNameAux = "";
			int fldid = 0;
			int occurance = 0;
			int fldtype = 0;
			int contadora = 0;
			int contadorb = 0;
			int contadorc = 0;

			// Recorrido del buffer
			while (fmliter.hasNext()) {

				entry = (java.util.Map.Entry) fmliter.next();
				fmlkey = (FmlKey) entry.getKey();

				// identificador fml
				fldid = fmlkey.get_fldid();

				// Obtener nombre del buffer
				try {
					fmlName = jrs.getResponse().Fname(fldid);
				} catch (Ferror ee) {
					jrs.done();
					throw new bea.jolt.pool.ServiceException(ee.getMessage(), ee);
				}
				contadora++;

				if ((!fmlNameAux.equals("")) && (fmlName.equals(fmlNameAux))) {
					// Se salta
					contadorb++;
					continue;
				} else {
					// Se trata
					contadorc++;
					fmlNameAux = fmlName;
				}

				// obtener ocurrencias
				occurance = jrs.getResponse().Foccur(fldid);

				// Si hay ocurrencias
				if (occurance > 0) {

					// Crear el vector de ocurrencias
					Vector vector = new Vector(occurance);

					tracer.traceln("Buffer [" + fmlName + "] Ocurrencias [" + occurance + "]", Tracer.TRACE_PARAM_LEVEL);

					// tipo
					fldtype = jrs.getResponse().Fldtype(fldid);

					// Para cada ocurrencia
					for (int i = 0; i < occurance; i++) {

						// Dependiendo del tipo castear adecuadamente y meter en
						// vector de ocurrencias
						try {
							switch (fldtype) {
								case TypedFML32.FLD_DOUBLE: {
									vector.addElement((Double) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_CHAR: {
									vector.addElement((Character) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_CARRAY: {
									vector.addElement((byte[]) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_FLOAT: {
									vector.addElement((Float) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_LONG: {
									vector.addElement((Integer) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_SHORT: {
									vector.addElement((Short) jrs.getResponse().Fget(fldid, i));
									break;
								}
								case TypedFML32.FLD_STRING: {
									vector.addElement((String) jrs.getResponse().Fget(fldid, i));
									break;
								}
							} // fin switch
						} catch (Ferror ee) {
							jrs.done();
							throw new bea.jolt.pool.ServiceException(ee.getMessage(), ee);
						}
					} // fin for (int i = 0; i < occurance; i++) {

					// meter en result el vector de ocurrencias para el buffer
					result.put(fmlName, vector);

				} // fin if (occurance > 0) {
			} // fin while (fmliter.hasNext()) {

			tracer.traceln("Ocurrencias totales [" + contadora + "]", Tracer.TRACE_PARAM_LEVEL);
			tracer.traceln("Ocurrencias saltadas [" + contadorb + "]", Tracer.TRACE_PARAM_LEVEL);
			tracer.traceln("Ocurrencias tratadas [" + contadorc + "]", Tracer.TRACE_PARAM_LEVEL);

			tracer.traceln("Fin recorrido buffer", Tracer.TRACE_INFO_LEVEL);

		} // fin if (jrs.getResponse() != null) {

		jrs.done();

		tracer.traceln("En SessionPool -> Servicio [" + service
				+ "] Obtenida la respuesta.", Tracer.TRACE_INFO_LEVEL);

		return result;
	}

	protected DataSet newDataSet(int i, boolean flag) {
		return new DataSet(i, flag);
	}

	protected Result newResult(int i) {
		return new Result(i);
	}

	protected void log(String s) {
		System.err.println(s);
	}

	boolean isKeepAlive() {
		return true;
	}

	int numConnections() {
		return 100;
	}

	void suspend(boolean flag) {
		joltSession.endSession();
	}

	Connection estConnect(Connection connection) {
		return connection;
	}

	private String[] getNameVal(String s) {
		int i;
		if ((i = s.indexOf('=')) < 0)
			return null;
		String as[] = new String[2];
		as[0] = s.substring(0, i);
		as[1] = s.substring(i + 1);
		if (as[1].length() == 0)
			as[1] = null;
		return as;
	}

	public JoltSession getJoltSession() {
		return joltSession;
	}

	public JoltDefinition getDefinition(String service) {
		String search = "addSVC/" + service;
		String service_definition = joltprop.getProperty(search, "NONE");
		tracer.traceln("Servicio [" + search + "] Definicion ["
				+ service_definition + "]", Tracer.TRACE_PARAM_LEVEL);
		return new JoltDefinition(search + ":" + service_definition);
	}

	private Object[] loadFileBuffers(String fileBufferNames)
	{
		System.out.println("DEBUG SessionPool loadFileBuffers[" + fileBufferNames + "]");

		if (fileBufferNames == null)
			return null;
		StringTokenizer token = new StringTokenizer(fileBufferNames, ";");
		Vector fileNames = new Vector(10, 10);
		String fileName = "";
		for (; token.hasMoreElements(); fileNames.add(fileName))
			fileName = token.nextToken();

		Object classes[] = new Object[fileNames.size()];
		for (int i = 0; i < fileNames.size(); i++)
			classes[i] = new DynamicFldTbl((String) fileNames.get(i), true);

		return classes;
	}

	private void setFieldTables(Object FMLTables[]) {
		if (FMLTables != null) {
			fieldTables = new DynamicFldTbl[FMLTables.length];
			for (int i = 0; i < FMLTables.length; i++)
				fieldTables[i] = (DynamicFldTbl) FMLTables[i];
		}
	}

	private boolean checkTimeOut(int timeout) {
		Date actualDate = new Date();
		long actualTime = actualDate.getTime();
		long creationTime = creationDate.getTime();
		return actualTime - creationTime >= (long) (timeout * 1000);
	}

	private synchronized void updateConf(boolean flag) {

		// si ya se ha puesto a true es que ya se esta actualizando
		// y no debe volver a hacerse
		if (updatingConf == true) {
			updatingConf = false;
		} else {
			updatingConf = flag;
		}

		// si se esta actualizando la configuracion
		if (updatingConf == true) {

			// volver a cargar el fichero de properties
			Properties fldprop_aux = PropertyLoader.loadProperties("gfijoltwtc");

			// si ha variado gfijoltwtc.properties darle el cambiazo
			// y continuar comprobando cambios
			if (!fldprop.equals(fldprop_aux)) {

				fldprop = fldprop_aux;

				// obtener variables del properties para el trazador
				String dirlog = fldprop.getProperty("dir_fich_log", "default");
				String nomlog = fldprop.getProperty("nom_fich_log", "default");
				int tracelevel = (new Integer(fldprop.getProperty("ntrazas", "0"))).intValue();

				// si ha cambiado el trazador
				if ((!tracer.getDirLog().equals(dirlog)) || (!tracer.getNomLog().equals(nomlog))) {
					// crear nuevo trazador
					Tracer tracer_aux = new Tracer(dirlog, nomlog);
					// darle el cambiazo
					tracer = tracer_aux;
				}

				if (tracer.getLogLevel() != tracelevel) {
					// cambiar nivel de trazas
					tracer.setLogLevel(tracelevel);
					tracer.traceln("Se ha actualizado el nivel de trazas.", Tracer.TRACE_PET_LEVEL);
				}

				int refreshConfTimeOut_aux = (new Integer(fldprop.getProperty("refresh_conf_timeout", "3600"))).intValue();
				if(this.refreshConfTimeOut != refreshConfTimeOut_aux) {
					// cambiar refreshConfTimeOut
					this.refreshConfTimeOut = refreshConfTimeOut_aux;
					tracer.traceln("Se ha actualizado el timeout de refresco de configuración [" + this.refreshConfTimeOut + "]", Tracer.TRACE_PET_LEVEL);
				}
			}

			// cargar los fieldtables genericos
			fmlFileNames = fldprop.getProperty("fldtables", "default");
			setFieldTables(loadFileBuffers(fmlFileNames));
			tracer.traceln("Se han actualizado los ficheros fld.", Tracer.TRACE_PET_LEVEL);

			// cargar jrepository
			Properties joltprop_aux = JoltRepositoryLoader.loadProperties("jrepository", tracer);
			//Properties joltprop_aux = PropertyLoader.loadProperties("jrepository");

			// si ha variado el jrepository darle el cambiazo
			if (!joltprop.equals(joltprop_aux)) {
				joltprop = joltprop_aux;
				tracer.traceln("Se ha actualizado el jrepository.", Tracer.TRACE_PET_LEVEL);
			}

			// nueva fecha de refresco
			creationDate = new Date();
			tracer.traceln("Último refresco de configuración.", Tracer.TRACE_PET_LEVEL);
		}
	}
}
