package bea.jolt.pool;

import java.util.Hashtable;

public class SessionPoolManager extends Hashtable
{
    private static final long serialVersionUID = 1L;
    public static SessionPoolManager poolmgr;
    private String poolName;

    public SessionPoolManager()
    {
        super(10);
        poolName = ".NONE";
    }

    public final void done()
    {
    }

    public final int createSessionPool(String as[], String as1[], int i, int j, UserInfo userinfo, String poolname)
    {
        SessionPool sessionpool = newSessionPool(null, null, 0, 0, null, false);
        poolName = ".NONAME";
        put(poolName, sessionpool);
        return 1000;
    }

    public final int createSession(String as[], UserInfo userinfo, String poolname)
    {
        SessionPool sessionpool = newSessionPool(null, null, 0, 0, null, false);
        poolName = ".NONAME";
        put(poolName, sessionpool);
        return 1000;
    }

    protected SessionPool newSessionPool(String as[], String as1[], int i, int j, UserInfo userinfo, boolean flag)
    {
    	System.out.println("dentro de newSessionPool");
        return new SessionPool(as, as1, i, j, userinfo, flag);
    }

    public final void suspendSessionPool(String s, boolean flag1)
    {
    }

    public final void stopSessionPool(String s)
    {
    }

    public final void removeSessionPool(String s)
    {
    }

    public final SessionPool getSessionPool(String poolname)
    {
        if(poolName.equals(".NONE"))
        {
        	System.out.println("dentro de getSessionPool -> antes de newSessionPool");
            SessionPool sessionpool = newSessionPool(null, null, 0, 0, null, false);
            poolName = ".NONAME";
            put(".NONAME", sessionpool);
            return (SessionPool)get(poolName);
        }
        else
        {
            return (SessionPool)get(poolName);
        }
    }
}