package bea.jolt.pool;

public class Result extends DataSet
{

    protected Result(int i)
    {
        super(i);
        applicationCode = 0;
    }

    public int getApplicationCode()
    {
        return applicationCode;
    }

    public void setApplicationCode(int a)
    {
        applicationCode = a;
    }

    private static final long serialVersionUID = 1L;
    protected int errtype;
    protected int errno;
    protected int detailErrno;
    protected int applicationCode;
    protected String errmsg;
    protected String detailErrmsg;
    protected static final int NO_ERROR = 0;
    protected static final int APPLICATION_ERROR = 1;
    protected static final int SYSTEM_ERROR = 2;
}
