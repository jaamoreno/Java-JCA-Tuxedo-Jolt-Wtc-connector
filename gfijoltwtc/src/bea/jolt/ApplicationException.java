package bea.jolt;


public class ApplicationException extends RuntimeException
{

    protected ApplicationException(int i, Object obj, String s)
    {
        super(s);
        e_errno = i;
        e_object = obj;
    }

    public int getApplicationCode()
    {
        return e_errno;
    }

    public Object getObject()
    {
        return e_object;
    }

    private static final long serialVersionUID = 1L;
    private Object e_object;
    private int e_errno;
}
