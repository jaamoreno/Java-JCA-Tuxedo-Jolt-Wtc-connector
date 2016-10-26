package bea.jolt.pool;


public class ServiceException extends bea.jolt.ServiceException
{

    public ServiceException(String s, Exception exception)
    {
        super(s, exception);
    }

    public ServiceException(int i, Object obj, String s, int j, String s1)
    {
        super(i, obj, s, j, s1);
    }

    private static final long serialVersionUID = 1L;
}
