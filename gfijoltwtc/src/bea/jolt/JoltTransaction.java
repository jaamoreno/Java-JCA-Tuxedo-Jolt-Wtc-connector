package bea.jolt;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.*;

public class JoltTransaction extends Transaction
{
    JoltSession joltSession;
    Context context;
    UserTransaction tx;


    public JoltTransaction(int timeout, Session session)
        throws TransactionException
    {
        joltSession = (JoltSession)session;
        joltSession.reconnectTuxedoConnection();

        context = ((JoltSession)session).getContext();
        try
        {
            tx = (UserTransaction)context.lookup("jta/UserTransaction");
        }
        catch(NamingException e)
        {
            throw new TransactionException("ERROR: Naming Exception looking up JNDI: " + e.getMessage());
        }

        if(timeout > 0)
            try
            {
                tx.setTransactionTimeout(timeout);
            }
            catch(SystemException e)
            {
                throw new TransactionException(e.getMessage());
            }

        try
        {
            tx.begin();
        }
        catch(NotSupportedException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(SystemException e)
        {
            throw new TransactionException(e.getMessage());
        }
    }

    public synchronized int commit() throws TransactionException
    {
        try
        {
            tx.commit();
        }
        catch(SecurityException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(IllegalStateException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(RollbackException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(HeuristicMixedException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(HeuristicRollbackException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(SystemException e)
        {
            throw new TransactionException(e.getMessage());
        }

        joltSession.reconnectTuxedoConnection();
        return 0;
    }

    public synchronized int rollback()
        throws TransactionException
    {
        try
        {
            tx.rollback();
        }
        catch(IllegalStateException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(SecurityException e)
        {
            throw new TransactionException(e.getMessage());
        }
        catch(SystemException e)
        {
            throw new TransactionException(e.getMessage());
        }
        joltSession.reconnectTuxedoConnection();
        return 0;
    }

    public UserTransaction getTransaction()
    {
        return tx;
    }

    public String GetStatus()
    {
    	int ret = javax.transaction.Status.STATUS_NO_TRANSACTION;
   		try
   		{
   			ret = tx.getStatus();
   		}
   		catch (SystemException e)
   		{
   			e.printStackTrace();
   		}

   		switch(ret)
   		{
   			case javax.transaction.Status.STATUS_ACTIVE:
   				return "ACTIVE";
   			case javax.transaction.Status.STATUS_COMMITTED:
   				return "COMMITTED";
   			case javax.transaction.Status.STATUS_COMMITTING:
   				return "COMMITTING";
   			case javax.transaction.Status.STATUS_MARKED_ROLLBACK:
   				return "MARKED_ROLLBACK";
   			case javax.transaction.Status.STATUS_NO_TRANSACTION:
   				return "NO_TRANSACTION";
   			case javax.transaction.Status.STATUS_PREPARED:
   				return "PREPARED";
   			case javax.transaction.Status.STATUS_PREPARING:
   				return "PREPARING";
   			case javax.transaction.Status.STATUS_ROLLEDBACK:
   				return "ROLLEDBACK";
  			case javax.transaction.Status.STATUS_ROLLING_BACK:
   				return "ROLLING_BACK";
   			case javax.transaction.Status.STATUS_UNKNOWN:
   				return "UNKNOWN";
   			default:
   				return "NO_TRANSACTION";
   		}
    }
}
