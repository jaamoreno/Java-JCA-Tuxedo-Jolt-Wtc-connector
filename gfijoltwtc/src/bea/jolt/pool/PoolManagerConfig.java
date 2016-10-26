package bea.jolt.pool;

import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;

public class PoolManagerConfig
{
    protected static SessionPoolManager w_poolmgr;

    protected static String startup(SessionPoolManager sessionpoolmanager, Hashtable hashtable)
        throws Exception
    {
        if(w_poolmgr == null)
            w_poolmgr = sessionpoolmanager;
        String poolname = "Unnamed";
        w_poolmgr.createSessionPool(null, null, 0, 0, null, ".NONAME");
        return "Jolt Session Pool: " + poolname + " pool started";
    }

    public static String shutdown(String poolname)
    {
        return "Jolt Session Pool: " + poolname + " pool shutdown";
    }

    public static Properties load(String s)
    {
        FileInputStream fileinputstream = null;
        Properties properties;
        try
        {
            fileinputstream = new FileInputStream(s);
            properties = new Properties();
            properties.load(fileinputstream);
        } catch(Exception e) {
            properties = null;
            e.printStackTrace();
        } finally {
        	if (fileinputstream != null) {
        		try {
        			fileinputstream.close();
        		} catch (Throwable ignore) {
        			ignore.printStackTrace();
        		}
        	}
		}

        return properties;
    }

    public PoolManagerConfig()
    {
    }
}
