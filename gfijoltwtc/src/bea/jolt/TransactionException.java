package bea.jolt;

public class TransactionException extends JoltException
{

    protected TransactionException(String s)
    {
        super(s);
    }

    protected TransactionException(String s, Exception exception)
    {
        super(s, exception);
    }

    protected TransactionException(int i, Object obj, String s, Exception exception)
    {
        super(i, obj, s, exception);
    }

    private static final long serialVersionUID = 1L;
}
