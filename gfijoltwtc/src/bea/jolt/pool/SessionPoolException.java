package bea.jolt.pool;

import bea.jolt.SessionException;

public class SessionPoolException extends SessionException
{

    protected SessionPoolException(String s, Exception exception)
    {
        super(s, exception);
    }

    protected SessionPoolException(int i, int j, Object obj, String s)
    {
        super(i, j, obj, s);
    }

    private static final long serialVersionUID = 1L;
}
