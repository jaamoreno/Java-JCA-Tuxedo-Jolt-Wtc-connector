package bea.jolt.pool;

import bea.jolt.Session;
import bea.jolt.JoltSession;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
// import javax.transaction.TransactionManager;
// (WAS-REDSYS) import weblogic.transaction.TransactionManager;

public class Transaction extends bea.jolt.Transaction
{
    JoltSession joltSession;
    Context context;
    UserTransaction txm;
    UserTransaction tx;

    public Transaction(int timeout, Session session)
        throws TransactionException
    {
    	System.out.println("bea.jolt.pool.Transaction(1)");
    	joltSession = (JoltSession)session;
    	System.out.println("bea.jolt.pool.Transaction(2)");
       	try
       	{
       		System.out.println("bea.jolt.pool.Transaction(3)");
       		context = joltSession.getContext();
       		System.out.println("bea.jolt.pool.Transaction(4)");
       		txm = (UserTransaction)context.lookup("jta/usertransaction");
       		System.out.println("bea.jolt.pool.Transaction(5)");
       	}
       	catch(NamingException e)
       	{
       		throw new TransactionException("ERROR: Naming Exception looking up JNDI: " + e.getMessage(), e);
       	}

        synchronized(tx)
        {
        	System.out.println("bea.jolt.pool.Transaction(6)");
        	tx = txm;
        	System.out.println("bea.jolt.pool.Transaction(7)");

        	if(timeout > 0)
        	try
            {
        		System.out.println("bea.jolt.pool.Transaction(8)");
        		tx.setTransactionTimeout(timeout);
        		System.out.println("bea.jolt.pool.Transaction(9)");
            }
            catch(SystemException e)
            {
            	throw new TransactionException(e.getMessage(), e);
            }
            try
            {
            	System.out.println("bea.jolt.pool.Transaction(10)");
            	tx.begin();
            	System.out.println("bea.jolt.pool.Transaction(11)");
            }
            catch(NotSupportedException e)
            {
            	throw new TransactionException(e.getMessage(), e);
            }
            catch(SystemException e)
            {
            	throw new TransactionException(e.getMessage(), e);
            }
        }

        System.out.println("bea.jolt.pool.Transaction(12)");
        joltSession.reconnectTuxedoConnection();
        System.out.println("bea.jolt.pool.Transaction(13)");
    }

    public synchronized int commit() throws TransactionException
    {
        try
        {
            tx.commit();
        }
        catch(SecurityException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(IllegalStateException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(RollbackException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(HeuristicMixedException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(HeuristicRollbackException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(SystemException e)
        {
            throw new TransactionException(e.getMessage(), e);
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
            throw new TransactionException(e.getMessage(), e);
        }
        catch(SecurityException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        catch(SystemException e)
        {
            throw new TransactionException(e.getMessage(), e);
        }
        joltSession.reconnectTuxedoConnection();
        return 0;
    }


	public javax.transaction.UserTransaction getTransaction(){
		return tx;
	}

	public javax.transaction.UserTransaction getTransactionManager(){
		//return this.txm;
		return txm;
	}

    public String GetStatus() {
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
