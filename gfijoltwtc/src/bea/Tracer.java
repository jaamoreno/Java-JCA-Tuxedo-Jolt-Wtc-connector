package bea;

import java.io.*;
import java.text.*;
import java.util.Date;

public class Tracer {

	public Tracer() {
		fullFileName = "";
		file = null;
		dirLog = "";
		nomLog = "";
		logLevel = 0;
		changeLogFileDate = null;
	}

	public Tracer(String dirLog, String nomLog) {
		file = null;
		this.dirLog = dirLog;
		this.nomLog = nomLog;
		logLevel = 0;
		changeLogFileDate = null;
		fullFileName = this.dirLog + "/" + this.nomLog;
		System.out.println("Inicializando trazador " + fullFileName);
		System.out.flush();
		allocate_log_file();
	}

	private void allocate_log_file() {
		SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String dateAsString = simpledateformat.format(date);
		String fullFileLog = fullFileName + "." + dateAsString + "."
				+ Math.abs(hashCode()) + ".log";
		try {
			if (file != null) {
				file.write("Cambiando a fichero de log: " + fullFileLog);
				file.close();
			} else {
				System.out.println("Abriendo fichero " + fullFileLog);
			}
			file = new FileWriter(fullFileLog, true);
			changeLogFileDate = simpledateformat.parse(dateAsString);
			return;
		} catch (Exception _ex) {
			file = null;
		}
		System.out.println("ERROR: no se pudo crear el fichero de log: "
				+ fullFileLog);
	}

	public void close() {
		if (file != null)
			try {
				file.close();
			} catch (IOException _ex) {
				System.out.println("En trace.close: IOException");
			}
	}

	public void flush() {
		if (file != null)
			synchronized (file) {
				try {
					file.flush();
				} catch (IOException ioexception) {
				}
			}
	}

	private String prepare_msg(String message, String date, int logLevel) {
		StringBuffer stringbuffer = new StringBuffer();
		stringbuffer.append(date);
		stringbuffer.append(" ");
		stringbuffer.append(logLevel);
		stringbuffer.append("/");
		stringbuffer.append(this.logLevel);
		stringbuffer.append(": ");
		stringbuffer.append(message);
		return stringbuffer.toString();
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public void trace(String message, int logLevel) {
		if (this.logLevel == 0 || logLevel <= this.logLevel)
			try {
				if (message == null)
					write_msg("message == null !!!!!\n", logLevel);
				else
					write_msg(message, logLevel);
			} catch (Exception exception) {
				write_msg(
						"---->[Tracer] ha habido problemas al tratar de escribir el mensaje: "
								+ exception.getMessage(), logLevel);
			}
	}

	public final void trace(Object aobj[], int logLevel) {
		if (this.logLevel == 0 || logLevel <= this.logLevel)
			try {
				if (aobj == null) {
					write_msg("message == null !!!!!", logLevel);
				} else {
					StringBuffer stringbuffer = new StringBuffer();
					for (int j = 0; j < aobj.length; j++)
						stringbuffer.append(aobj[j]);

					write_msg(stringbuffer.toString(), logLevel);
				}
			} catch (Exception exception) {
				write_msg(
						"---->[Tracer] ha habido problemas al tratar de escribir el mensaje: "
								+ exception.getMessage(), logLevel);
			}
	}

	public final void traceException(Throwable throwable, int logLevel) {
		if (this.logLevel == 0 || logLevel <= this.logLevel) {
			StringBuffer stringbuffer = new StringBuffer();
			stringbuffer.append("--> EXCEPCION [");
			stringbuffer.append(throwable.getClass().getName());
			stringbuffer.append("]: ");
			stringbuffer.append(throwable.getMessage());
			stringbuffer.append("\n");
			write_msg(stringbuffer.toString(), logLevel);
			if (file != null) {
				synchronized (file) {
					PrintWriter printwriter = new PrintWriter(file);
					throwable.printStackTrace(printwriter);
					if (printwriter.checkError())
						throwable.printStackTrace();
					printwriter.close();
				}
			}
			else
				System.out.println("-----Llamada a traceException ["
						+ throwable + "]");
		}
	}

	public void traceln(String message, int logLevel) {
		if (this.logLevel == 0 || logLevel <= this.logLevel)
			try {
				if (message == null)
					write_msg("message == null !!!!!\n", logLevel);
				else
					write_msg(message + "\n", logLevel);
			} catch (Exception exception) {
				write_msg(
						"---->[Tracer] ha habido problemas al tratar de escribir el mensaje: "
								+ exception.getMessage(), logLevel);
				exception.printStackTrace();
			}
	}

	public void traceln(Object aobj[], int logLevel) {
		if (this.logLevel == 0 || logLevel <= this.logLevel)
			try {
				if (aobj == null) {
					write_msg("message == null !!!!!\n", logLevel);
				} else {
					StringBuffer stringbuffer = new StringBuffer();
					for (int j = 0; j < aobj.length; j++)
						stringbuffer.append(aobj[j]);

					stringbuffer.append("\n");
					write_msg(stringbuffer.toString(), logLevel);
				}
			} catch (Exception exception) {
				write_msg(
						"---->[Tracer] ha habido problemas al tratar de escribir el mensaje: "
								+ exception.getMessage(), logLevel);
				exception.printStackTrace();
			}
	}

	private final void write_msg(String message, int log_level) {
		Date date = new Date();

		SimpleDateFormat simpledateformat = new SimpleDateFormat("HH:mm:ss:SSS");

		String prepared_msg = prepare_msg(message, simpledateformat.format(date), log_level);

		simpledateformat = new SimpleDateFormat("yyyyMMdd");

		if (file != null) {
			synchronized (file) {
				try {
					date = simpledateformat.parse(simpledateformat.format(date));

					if (date.after(changeLogFileDate))
						allocate_log_file();
				} catch (ParseException parseexception) {
				}

				try {
					file.write(prepared_msg);
					file.flush();
				} catch (IOException ioexception) {
					System.out
							.println("---->[Tracer] Error escribiendo a fichero");
					ioexception.printStackTrace();
				}
			}
		} else {
			System.out.print(prepared_msg);
		}
	}

	public String getDirLog() {
		return dirLog;
	}

	public void setDirLog(String dirLog) {
		this.dirLog = dirLog;
	}

	public String getNomLog() {
		return nomLog;
	}

	public void setNomLog(String nomLog) {
		this.nomLog = nomLog;
	}

	private String fullFileName;

	private String dirLog;

	private String nomLog;

	private int logLevel;

	private FileWriter file;

	private Date changeLogFileDate;

	public static final int TRACE_ERR_LEVEL = 0;

	public static final int TRACE_SYSTEM_LEVEL = 1;

	public static final int TRACE_PET_LEVEL = 3;

	public static final int TRACE_INFO_LEVEL = 4;

	public static final int TRACE_PARAM_LEVEL = 5;
}
