package bea.jolt.pool;

public class TransactionException extends bea.jolt.TransactionException
{
    private static final long serialVersionUID = 1L;

    public TransactionException(String s, Exception exception)
    {
        super(s, exception);
    }

    public TransactionException(int i, String s, Exception exception)
    {
        super(i, null, s, exception);
    }
}
