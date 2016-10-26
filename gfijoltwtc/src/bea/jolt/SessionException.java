package bea.jolt;

public class SessionException extends JoltException
{

    protected SessionException(String s)
    {
        super(s);
    }

    protected SessionException(String s, Exception exception)
    {
        super(s, exception);
    }

    protected SessionException(int i, int j, Object obj, String s)
    {
        super(j, obj, s, null);
        e_urcode = i;
    }

    protected SessionException(int i, Object obj, String s, Exception exception)
    {
        super(i, obj, s, exception);
    }

    public int getApplicationCode()
    {
        return e_urcode;
    }

    private static final long serialVersionUID = 1L;
    private int e_urcode;
}
