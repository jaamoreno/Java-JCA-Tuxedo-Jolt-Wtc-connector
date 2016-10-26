package bea.jolt;

import bea.DynamicFldTbl;
import bea.PropertyLoader;
import bea.Tracer;
import java.util.*;
import weblogic.wtc.jatmi.*;
import com.oracle.tuxedo.adapter.cci.TuxedoInteraction;


public class JoltRemoteService
{
	private String serviceName;
	private String fmlFileNames;
	private TuxedoInteraction ix;

	private DynamicFldTbl fieldTables[];
	private TypedFML32 request;
	private TypedFML32 response;
	private Properties prop;
	private Tracer tracer;


	public JoltRemoteService(String service, Session session)
			throws ServiceException {
		serviceName = null;
		fmlFileNames = null;
		ix = null;
		fieldTables = null;
		request = null;
		response = null;
		prop = null;
		tracer = null;
		initJoltRemoteService(service, session);
	}

	public JoltRemoteService(String service, Session session, int i)
			throws ServiceException, JoltVersionException {
		serviceName = null;
		fmlFileNames = null;
		ix = null;
		fieldTables = null;
		request = null;
		response = null;
		tracer = null;
		try
		{
			this.prop = PropertyLoader.loadProperties("gfijoltwtc");
		}
		catch (IllegalArgumentException e)
		{
			throw new ServiceException(e.getMessage());
		}
		initJoltRemoteService(service, session);
	}

	public JoltRemoteService(String service, Session session, Properties prop,
			Tracer tracer, DynamicFldTbl fieldTables[])
			throws ServiceException, JoltVersionException {
		serviceName = null;
		fmlFileNames = null;
		ix = null;
		request = null;
		response = null;

		if (prop == null) {
			try
			{
				this.prop = PropertyLoader.loadProperties("gfijoltwtc");
			}
			catch (IllegalArgumentException e)
			{
				throw new ServiceException(e.getMessage());
			}
		} else {
			this.prop = prop;
		}

		this.tracer = tracer;
		initJoltRemoteService(service, session, fieldTables);
	}

	private void initJoltRemoteService(String service, Session session)
			throws ServiceException
	{
		ix = ((JoltSession) session).getTuxedoConnection();
		serviceName = service;

		fmlFileNames = prop.getProperty(service, "fldtables");

		if (fmlFileNames.equals("fldtables"))
			fmlFileNames = prop.getProperty("fldtables", "default");

		setFieldTables(loadFileBuffers(fmlFileNames));

		request = new TypedFML32(fieldTables);
		response = new TypedFML32(fieldTables);
	}

	private void initJoltRemoteService(String service, Session session,
			DynamicFldTbl fieldTables[]) throws ServiceException
	{
		ix = ((JoltSession) session).getTuxedoConnection();
		serviceName = service;

		// Si fmlFileNames son especificos cargarlos ahora
		// sino se utilizan los genericos recibidos de SessionPool
		fmlFileNames = prop.getProperty(service, "fldtables");

		if (fmlFileNames.equals("fldtables")) {
			// valen los recibidos
			setFieldTables(fieldTables);
		} else {
			// se cargan los especificos
			setFieldTables(loadFileBuffers(fmlFileNames));
		}

		request = new TypedFML32(this.fieldTables);
		response = new TypedFML32(this.fieldTables);
	}

	private Object[] loadFileBuffers(String fileBufferNames)
	{
		System.out.println("DEBUG JoltRemoteService loadFileBuffers[" + fileBufferNames + "]");

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

	public void done() {
		if (request != null) {
			request = null;
		}
		if (response != null) {
			response = null;
		}
	}

	public int getRequestType() {
		return 7;
	}

	public String getName() {
		return serviceName;
	}

	public int getApplicationCode() {
		return 0;
	}

	public JoltDefinition getDefinition() {
		return null;
	}


	public void call(Transaction transaction)
		throws ServiceException, TransactionException, ApplicationException
	{
		Reply reply = null;
		try
		{
			if (transaction == null)
			{
				tracer.traceln("Se ejecuta servicio [" + serviceName+ "] sin transacción", Tracer.TRACE_PET_LEVEL);
				reply = ix.tpcall(serviceName, request, /* TuxedoConnection.TPNOTRAN */ 8 );
				tracer.traceln("Se ha ejecutado servicio [" + serviceName+ "] sin transacción", Tracer.TRACE_PET_LEVEL);
			}
			else
			{
				tracer.traceln("Se ejecuta servicio [" + serviceName+ "] con transacción",Tracer.TRACE_PET_LEVEL);
				reply = ix.tpcall(serviceName, request, 0);
				tracer.traceln("Se ha ejecutado servicio [" + serviceName+ "] con transacción",Tracer.TRACE_PET_LEVEL);
			}
		}
		catch (TPReplyException e)
		{
			tracer.traceln(
					"Excepción TPReplyException en llamada al servicio ["
							+ serviceName + "]", Tracer.TRACE_PET_LEVEL);
			e.printStackTrace();

			if (e.gettperrno() == TPReplyException.TPESVCFAIL) {
				tracer.traceln("Excepción del tipo TPESVCFAIL", Tracer.TRACE_PET_LEVEL);

				reply = e.getReplyRtn();

				if (reply != null) {
					tracer.traceln("Hay respuesta", Tracer.TRACE_INFO_LEVEL);
					response = (TypedFML32)e.getReplyRtn().getReplyBuffer();
					response.setFieldTables(this.fieldTables);
				} else {
					tracer.traceln("No hay respuesta", Tracer.TRACE_INFO_LEVEL);
					response = request;
					response.setFieldTables(this.fieldTables);
				}
				tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
				tracer.traceln(TPReplyException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
				throw new ApplicationException(e.gettperrno(), this, TPReplyException.tpstrerror(e.gettperrno()));
			}

			if (e.gettperrno() == TPReplyException.TPESVCERR) {
				tracer.traceln("Excepción del tipo TPESVCERR", Tracer.TRACE_PET_LEVEL);
				tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
				tracer.traceln(TPReplyException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
				throw new ServiceException(e.gettperrno(), this, TPReplyException.tpstrerror(e.gettperrno()), e);
			}

			if (transaction != null) {
				tracer.traceln("Hay transacción", Tracer.TRACE_PET_LEVEL);
				if ((e.gettperrno() == TPReplyException.TPETIME)
						|| (e.gettperrno() == TPReplyException.TPETRAN)
						|| (e.gettperrno() == TPReplyException.TPEABORT)) {
					tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
					tracer.traceln(TPReplyException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
					throw new TransactionException(e.gettperrno(), this, TPReplyException.tpstrerror(e.gettperrno()), e);
				}
			}

			tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
			tracer.traceln(TPReplyException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
			throw new ServiceException(e.gettperrno(), this, TPReplyException.tpstrerror(e.gettperrno()), e);

		} catch (TPException e) {
			tracer.traceln("Excepción TPException en llamada al servicio ["
					+ serviceName + "]", Tracer.TRACE_PET_LEVEL);
			e.printStackTrace();

			if (transaction != null) {
				tracer.traceln("Hay transacción",Tracer.TRACE_PET_LEVEL);
				if ((e.gettperrno() == TPException.TPETIME)
						|| (e.gettperrno() == TPException.TPETRAN)
						|| (e.gettperrno() == TPException.TPEABORT)) {
					tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
					tracer.traceln(TPException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
					throw new TransactionException(e.gettperrno(), this, TPException.tpstrerror(e.gettperrno()), e);
				}
			}

			tracer.traceln(e.getMessage(), Tracer.TRACE_PET_LEVEL);
			tracer.traceln(TPException.tpstrerror(e.gettperrno()), Tracer.TRACE_PET_LEVEL);
			throw new ServiceException(e.gettperrno(), this, TPException.tpstrerror(e.gettperrno()), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Excepción Genérica Inesperada.", e);
		}

		if (reply != null) {

			response = (TypedFML32) reply.getReplyBuffer();

			if (response != null) {
				response.setFieldTables(this.fieldTables);
			} else {
				tracer.traceln("Response es nulo en llamada al servicio ["
						+ serviceName + "]", Tracer.TRACE_INFO_LEVEL);
			}
		} else {
			tracer.traceln("Reply  es nulo en llamada al servicio ["
					+ serviceName + "]", Tracer.TRACE_INFO_LEVEL);
			response = request;
			response.setFieldTables(this.fieldTables);
		}
		}

		public String toString() {
			return null;
		}

		public void clear() {
			request = new TypedFML32(fieldTables);
			response = new TypedFML32(fieldTables);
		}

		public final void addField(String name, Object val) throws Exception {
		try {
			int id = request.Fldid(name);
			int type = request.Fldtype(id);

			if ((val instanceof Short) && type == TypedFML32.FLD_LONG) {

				Integer valInteger = new Integer(((Short) val).intValue());
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Short]->[Long], Valor [" + (Short) val
						+ "]->[" + valInteger + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, valInteger);

			} else if ((val instanceof Integer) && type == TypedFML32.FLD_SHORT) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Integer]->[Short] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, val);

			} else if ((val instanceof Short) && type == TypedFML32.FLD_STRING) {

				String valString = ((Short) val).toString();
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Short]->[String], Valor [" + (Short) val
						+ "]->[" + valString + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, valString);

			} else if ((val instanceof Integer) && type == TypedFML32.FLD_STRING) {

				String valString = ((Integer) val).toString();
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Long]->[String], Valor [" + (Short) val
						+ "]->[" + valString + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, valString);


			} else if ((val instanceof String) && type == TypedFML32.FLD_SHORT) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [String]->[Short] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, val);

			} else if ((val instanceof String) && type == TypedFML32.FLD_LONG) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [String]->[Long] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, 0, val);

			} else {
				request.Fchg(id, 0, val);
			}
		} catch (Ferror e) {
			e.printStackTrace();
			tracer.traceln("Servicio [" + serviceName
					+ "] Error al establecer buffer entrada [" + name + "]",
					Tracer.TRACE_PET_LEVEL);
			throw new Exception(Ferror.Fstrerror(e.getFerror()));
		} catch(Exception e) {
			e.printStackTrace();
			tracer.traceln("Servicio [" + serviceName
					+ "] Error al establecer buffer entrada [" + name + "]",
					Tracer.TRACE_PET_LEVEL);
			throw new Exception(e.getMessage());
		}
		}

		public final void setField(String name, int occurance, Object val)
			throws Exception {
		try {
			int id = request.Fldid(name);
			int type = request.Fldtype(id);

			if ((val instanceof Short) && type == TypedFML32.FLD_LONG) {

				Integer valInteger = new Integer(((Short) val).intValue());
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Short]->[Long], Valor [" + (Short) val
						+ "]->[" + valInteger + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, valInteger);

			} else if ((val instanceof Integer) && type == TypedFML32.FLD_SHORT) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Integer]->[Short] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, val);

			} else if ((val instanceof Short) && type == TypedFML32.FLD_STRING) {

				String valString = ((Short) val).toString();
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Short]->[String], Valor [" + (Short) val
						+ "]->[" + valString + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, valString);

			} else if ((val instanceof Integer) && type == TypedFML32.FLD_STRING) {

				String valString = ((Integer) val).toString();
				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [Long]->[String], Valor [" + (Short) val
						+ "]->[" + valString + "] Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, valString);


			} else if ((val instanceof String) && type == TypedFML32.FLD_SHORT) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [String]->[Short] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, val);

			} else if ((val instanceof String) && type == TypedFML32.FLD_LONG) {

				tracer.traceln("Servicio [" + serviceName
						+ "] Conv [String]->[Long] No Tratado",
						Tracer.TRACE_PET_LEVEL);
				request.Fchg(id, occurance, val);

			} else {
				request.Fchg(id, occurance, val);
			}
		} catch (Ferror e) {
			e.printStackTrace();
			tracer.traceln("Servicio [" + serviceName
					+ "] Error al establecer buffer entrada [" + name + "]",
					Tracer.TRACE_PET_LEVEL);
			throw new Exception(Ferror.Fstrerror(e.getFerror()));

		} catch(Exception e) {
			e.printStackTrace();
			tracer.traceln("Servicio [" + serviceName
				+ "] Error al establecer buffer entrada [" + name + "]",
				Tracer.TRACE_PET_LEVEL);
			throw new Exception(e.getMessage());
		}
		}

		public void addByte(String name, byte val) throws Exception {
		Character newValue = new Character((char) val);
		addField(name, newValue);
		}

		public void addShort(String name, short val) throws Exception {
			Short newValue = new Short(val);
			addField(name, newValue);
		}

		public void addInt(String name, int val) throws Exception {
			Integer newValue = new Integer(val);
			addField(name, newValue);
		}

		public void addFloat(String name, float val) throws Exception {
			Float newValue = new Float(val);
			addField(name, newValue);
		}

		public void addDouble(String name, double val) throws Exception {
			Double newValue = new Double(val);
			addField(name, newValue);
		}

		public void addString(String name, String val) throws Exception {
			addField(name, val);
		}

		public void addBytes(String name, byte val[], int len) throws Exception {
			addField(name, val);
		}

		public void setByte(String name, byte val) throws Exception {
			Character newValue = new Character((char) val);
			setField(name, 0, newValue);
		}

		public void setShort(String name, short val) throws Exception {
			Short newValue = new Short(val);
			setField(name, 0, newValue);
		}

		public void setInt(String name, int val) throws Exception {
			Integer newValue = new Integer(val);
			setField(name, 0, newValue);
		}

		public void setFloat(String name, float val) throws Exception {
			Float newValue = new Float(val);
			setField(name, 0, newValue);
		}

		public void setDouble(String name, double val) throws Exception {
			Double newValue = new Double(val);
			setField(name, 0, newValue);
		}

		public void setString(String name, String val) throws Exception {
			setField(name, 0, val);
		}

		public void setBytes(String name, byte val[], int i) throws Exception {
			setField(name, 0, val);
		}

		public void setByteItem(String name, int i, byte val) throws Exception {
			Character newValue = new Character((char) val);
			setField(name, i, newValue);
		}

		public void setShortItem(String name, int i, short val) throws Exception {
			Short newValue = new Short(val);
			setField(name, i, newValue);
		}

		public void setIntItem(String name, int i, int val) throws Exception {
			Integer newValue = new Integer(val);
			setField(name, i, newValue);
		}

		public void setFloatItem(String name, int i, float val) throws Exception {
			Float newValue = new Float(val);
			setField(name, i, newValue);
		}

		public void setDoubleItem(String name, int i, double val) throws Exception {
			Double newValue = new Double(val);
			setField(name, i, newValue);
		}

		public void setStringItem(String name, int i, String val) throws Exception {
			setField(name, i, val);
		}

		public void setBytesItem(String name, int i, byte val[], int j)
			throws Exception {
			setField(name, i, val);
		}

		public void delete(String name) {
			deleteItem(name, 0);
		}

		public void deleteItem(String name, int i) {
			try {
				int id = request.Fldid(name);
				request.Fdel(id, i);
			} catch (Ferror e) {
				throw new NoSuchFieldError(name);
			}
		}

		public int getOccurrenceCount(String name) {
			int id = 0;
			try {
				id = response.Fldid(name);
			} catch (Ferror e) {
				throw new NoSuchFieldError(name);
			}
			return request.Foccur(id);
		}

		private final Object getField(String name, int occurance) {
			int id = 0;
			Object object = null;
			try {
				id = response.Fldid(name);
				object = response.Fget(id, occurance);
			} catch (Ferror e) {
				throw new NoSuchFieldError(name);
			}
			return object;
		}

		public byte getByteDef(String name, byte def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				Character newValue = (Character) fieldObject;
				return (byte) newValue.charValue();
			} else {
				return def;
			}
		}

		public short getShortDef(String name, short def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				Short newValue = (Short) fieldObject;
				return newValue.shortValue();
			} else {
				return def;
			}
		}

		public int getIntDef(String name, int def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				Integer newValue = (Integer) fieldObject;
				return newValue.intValue();
			} else {
				return def;
			}
		}

		public float getFloatDef(String name, float def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				Float newValue = (Float) fieldObject;
				return newValue.floatValue();
			} else {
				return def;
			}
		}

		public double getDoubleDef(String name, double def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				Double newValue = (Double) fieldObject;
				return newValue.doubleValue();
			} else {
				return def;
			}
		}

		public String getStringDef(String name, String def) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				String newValue = (String) fieldObject;
				return newValue;
			} else {
				return def;
			}
		}

		public byte[] getBytesDef(String name, byte def[]) {
			Object fieldObject = getField(name, 0);
			if (fieldObject != null) {
				byte newValue[] = (byte[]) fieldObject;
				return newValue;
			} else {
				return def;
			}
		}

		public byte getByteItemDef(String name, int i, byte def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				Character newValue = (Character) fieldObject;
				return (byte) newValue.charValue();
			} else {
				return def;
			}
		}

		public short getShortItemDef(String name, int i, short def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				Short newValue = (Short) fieldObject;
				return newValue.shortValue();
			} else {
				return def;
			}
		}

		public int getIntItemDef(String name, int i, int def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				Integer newValue = (Integer) fieldObject;
				return newValue.intValue();
			} else {
				return def;
			}
		}

		public float getFloatItemDef(String name, int i, float def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				Float newValue = (Float) fieldObject;
				return newValue.floatValue();
			} else {
				return def;
			}
		}

		public double getDoubleItemDef(String name, int i, double def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				Double newValue = (Double) fieldObject;
				return newValue.doubleValue();
			} else {
				return def;
			}
		}

		public String getStringItemDef(String name, int i, String def) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				String newValue = (String) fieldObject;
				return newValue;
			} else {
				return def;
			}
		}

		public byte[] getBytesItemDef(String name, int i, byte def[]) {
			Object fieldObject = getField(name, i);
			if (fieldObject != null) {
				byte newValue[] = (byte[]) fieldObject;
				return newValue;
			}
			else
			{
				return def;
			}
		}

		public JoltMessage getInputs() {
			return null;
		}

		public JoltMessage getOutputs() {
			return null;
		}

		public TypedFML32 getResponse() {
			return response;
		}

		public TypedFML32 getRequest() {
			return request;
		}

		public void printRequestFieldTables() {
		int numFieldTables = request.getFieldTables().length;
		for (int i = 0; i < numFieldTables; i++) {
			int numFldNames = request.getFieldTables()[i].getFldNames().length;
			for (int j = 0; j < numFldNames; j++)
				System.out
						.println(request.getFieldTables()[i].getFldNames()[j]);
		}
		}

		public void printResponseFieldTables() {
		int numFieldTables = response.getFieldTables().length;
		for (int i = 0; i < numFieldTables; i++) {
			int numFldNames = response.getFieldTables()[i].getFldNames().length;
			for (int j = 0; j < numFldNames; j++)
				System.out
						.println(response.getFieldTables()[i].getFldNames()[j]);
		}
	}

	public void setTuxedoConnection(Session session)
	{
		ix = ((JoltSession) session).getTuxedoConnection();
	}
}
