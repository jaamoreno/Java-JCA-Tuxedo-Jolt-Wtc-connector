package bea.jolt;

public class ServiceException extends JoltException
{

    protected ServiceException(String s)
    {
        super(s);
    }

    protected ServiceException(String s, Exception exception)
    {
        super(s, exception);
    }

    protected ServiceException(int i, Object obj, String s, int j, String s1)
    {
        super(i, obj, s);
        e_errno_detail = j;
        e_string_errno_detail = s1;
    }

    protected ServiceException(int i, Object obj, String s, Exception exception)
    {
        super(i, obj, s, exception);
    }

    public int getErrorDetail()
    {
        return e_errno_detail;
    }

    public String getStringErrorDetail()
    {
        return e_string_errno_detail;
    }

    private static final long serialVersionUID = 1L;
    private int e_errno_detail;
    private String e_string_errno_detail;
}
