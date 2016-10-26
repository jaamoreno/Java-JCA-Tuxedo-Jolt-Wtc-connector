package bea.jolt;


public class JoltException extends RuntimeException
{

    public JoltException(String s)
    {
        super(s);
        e_errno = 100;
        e_exception = null;
    }

    public JoltException(String s, Exception exception)
    {
        super(s);
        e_errno = 100;
        e_exception = exception;
    }

    public JoltException(int i, Object obj, Exception exception)
    {
        e_errno = i;
        e_object = obj;
        e_exception = exception;
    }

    public JoltException(int i, Object obj, String s)
    {
        super(s);
        e_errno = i;
        e_object = obj;
        e_exception = null;
    }

    public JoltException(int i, Object obj, String s, Exception exception)
    {
        super(s);
        e_errno = i;
        e_object = obj;
        e_exception = exception;
    }

    public int getErrno()
    {
        return e_errno;
    }

    public Object getObject()
    {
        return e_object;
    }

    public String toString()
    {
        return e_exception == null ? super.toString() : super.toString() + "\\n" + e_exception.toString();
    }

    private static final long serialVersionUID = 1L;
    public static final int TPEABORT = 1;
    public static final int TPEBADDESC = 2;
    public static final int TPEBLOCK = 3;
    public static final int TPEINVAL = 4;
    public static final int TPELIMIT = 5;
    public static final int TPENOENT = 6;
    public static final int TPEOS = 7;
    public static final int TPEPERM = 8;
    public static final int TPEPROTO = 9;
    public static final int TPESVCERR = 10;
    public static final int TPESVCFAIL = 11;
    public static final int TPESYSTEM = 12;
    public static final int TPETIME = 13;
    public static final int TPETRAN = 14;
    public static final int TPGOTSIG = 15;
    public static final int TPERMERR = 16;
    public static final int TPEITYPE = 17;
    public static final int TPEOTYPE = 18;
    public static final int TPERELEASE = 19;
    public static final int TPEHAZARD = 20;
    public static final int TPEHEURISTIC = 21;
    public static final int TPEEVENT = 22;
    public static final int TPEMATCH = 23;
    public static final int TPEDIAGNOSTIC = 24;
    public static final int TPEMIB = 25;
    public static final int TPEJOLT = 100;
    public static final int TPEIO = 101;
    private int e_errno;
    private Object e_object;
    private Exception e_exception;
}
