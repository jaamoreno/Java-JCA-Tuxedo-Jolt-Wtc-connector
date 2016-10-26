package bea.jolt;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import com.oracle.tuxedo.adapter.cci.TuxedoConnectionFactory;
import com.oracle.tuxedo.adapter.cci.TuxedoJCAConnection;
import com.oracle.tuxedo.adapter.cci.TuxedoInteraction;;


public class JoltSession extends Session
{
	private Context ctx;
	private TuxedoConnectionFactory cf;
	private TuxedoJCAConnection  c;
	private TuxedoInteraction ix;

	public JoltSession()
    {
        ctx = null;
        cf = null;
        c = null;
        ix = null;

        try
        {
        	ctx = new InitialContext();
        	cf = (TuxedoConnectionFactory)ctx.lookup("tuxedo/services/TuxedoConnection");
        }
        catch(NamingException e)
        {
            e.printStackTrace();
        }

        try
        {
        	c  = (TuxedoJCAConnection) cf.getConnection();
        	ix = (TuxedoInteraction) c.createInteraction();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public JoltSession(JoltSessionAttributes attr, String string, String string2, String string3, String string4)
    {
        ctx = null;
        cf = null;
        c = null;
        ix = null;

        try
        {
            ctx = new InitialContext();
            cf = (TuxedoConnectionFactory)ctx.lookup("tuxedo/services/TuxedoConnection");
        }
        catch(NamingException e)
        {
            e.printStackTrace();
        }

        try
        {
        	c = (TuxedoJCAConnection) cf.getConnection();
        	ix = (TuxedoInteraction) c.createInteraction();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public void endSession()
    {
        if(c != null)
        {
			try
			{
				ix.tpterm();
				c.close();
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
        }
    }

    public void reconnectTuxedoConnection()
    {
        try
        {
            synchronized(this)
            {
                if(cf == null)
                {
                	cf = (TuxedoConnectionFactory)ctx.lookup("tuxedo/services/TuxedoConnection");
                }
            }
        }
        catch(NamingException e)
        {
            e.printStackTrace();
        }

        try
        {
        	c = (TuxedoJCAConnection)cf.getConnection();
        	ix = (TuxedoInteraction) c.createInteraction();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public TuxedoInteraction getTuxedoConnection()
    {
        // if(tuxedoConnection == null)  /* siempre se hace el reconnect */
            reconnectTuxedoConnection();

        return ix;
    }


    public Context getContext()
    {
        return ctx;
    }
}
