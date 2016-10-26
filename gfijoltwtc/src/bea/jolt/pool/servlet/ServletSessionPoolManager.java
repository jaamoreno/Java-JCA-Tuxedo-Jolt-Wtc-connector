package bea.jolt.pool.servlet;

import bea.jolt.pool.*;

public class ServletSessionPoolManager extends SessionPoolManager
{
    private static final long serialVersionUID = 1L;

    public ServletSessionPoolManager()
    {
    }

    protected final SessionPool newSessionPool(String as[], String as1[], int i, int j, UserInfo userinfo, boolean flag)
    {
        return new ServletSessionPool(as, as1, i, j, userinfo, flag);
    }

    public static void main(String args[])
        throws Exception
    {
        Object obj = SessionPoolManager.poolmgr;
        if(obj == null)
            SessionPoolManager.poolmgr = new ServletSessionPoolManager();
        UserInfo userinfo = new UserInfo();
        if(SessionPoolManager.poolmgr.createSessionPool(null, null, 0, 0, userinfo, "") < 100)
        {
            SessionPoolManager.poolmgr.removeSessionPool(".NONAME");
            throw new Exception("Cannot create Unnamed pool");
        } else
        {
            System.out.println("Jolt Session Pool: Unnamed pool started");
            return;
        }
    }
}
