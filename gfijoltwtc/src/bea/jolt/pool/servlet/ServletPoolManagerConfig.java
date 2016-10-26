package bea.jolt.pool.servlet;

import bea.jolt.pool.PoolManagerConfig;
import java.util.Hashtable;

public class ServletPoolManagerConfig extends PoolManagerConfig
{

    public static String startup(Hashtable hashtable)
        throws Exception
    {
        if(PoolManagerConfig.w_poolmgr == null)
            PoolManagerConfig.w_poolmgr = new ServletSessionPoolManager();
        return PoolManagerConfig.startup(PoolManagerConfig.w_poolmgr, hashtable);
    }

    public static ServletSessionPoolManager getSessionPoolManager()
    {
        return (ServletSessionPoolManager)PoolManagerConfig.w_poolmgr;
    }

    public ServletPoolManagerConfig()
    {
    }
}
