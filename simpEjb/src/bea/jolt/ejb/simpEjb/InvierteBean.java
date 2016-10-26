package bea.jolt.ejb.simpEjb;
/*	(c) 2003 BEA Systems, Inc. All Rights Reserved. */

import bea.jolt.pool.*;
import java.rmi.RemoteException;
import javax.ejb.*;



public class InvierteBean implements SessionBean
{
	private static final long serialVersionUID = 1L;
	// private variables
	private static SessionPoolManager sessionPoolManager = null;
	private SessionPool sessionPool = null;


  // -----------------------------------------------------------------
  // SessionBean implementation
  public void ejbActivate() throws EJBException, RemoteException {}
  {
  }

  public void ejbRemove() throws EJBException, RemoteException {}
  {
  }

  public void ejbPassivate() throws EJBException, RemoteException {}
  {
  }


  public void ejbCreate()
  {
   	    System.out.println("ejbCreate");
  }

	// Nuestro método "de negocio"
	public String invierte(String cadena)
	{
		Result res;
	    DataSet request = new DataSet();
	    request.setValue("MyString", cadena);
	    Transaction tran = null;
	    String salida = new String();

		try
		{
			initJoltPool();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    try
	    {
          System.out.println("antes startTransaction");
          try
          {
        	  //tran = sessionPool.startTransaction(15);
        	  System.out.println("canonicalname:" + Transaction.class.getCanonicalName());
          }
          catch(Throwable t)
          {
        	  System.out.println("excepcion de startTransaction(30)");
        	  t.printStackTrace();
        	  System.out.println("---------");
        	  t.getMessage();
        	  System.out.println("---------");
          }
          System.out.println("(NO) despues startTransaction");
	      res = sessionPool.call("REVERSE_STRING", request, tran);
		  System.out.println("invierte(6). Despues de la llamada");
	      res.getValue("MyString", salida);
		  System.out.println("invierte(7)");
	    }
	   catch (TransactionException te)
	    {
		  System.out.println("excepcion -> " + te.getMessage());
	      throw new TransactionException("Transaction exception ", te);
	    }

		return salida;
	}

	public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {};

	private void initJoltPool() throws Exception
    {
		System.out.println("initJoltPool(1).");
		sessionPoolManager = SessionPoolManager.poolmgr;
		System.out.println("initJoltPool(2).");

	    try
	    {
	    	System.out.println("initJoltPool(3).");
			sessionPool = sessionPoolManager.getSessionPool(".NONE");
			System.out.println("initJoltPool(4).");
	    }
	    catch(Throwable e)
	    {
	    	System.out.println("excepcion 3-4");
	    	System.out.println("getMessage -> " + e.getMessage());
	    	e.printStackTrace();
	    }

	    System.out.println("initJoltPool(5).");
	    if (sessionPool == null)
	    {
	      throw new Exception("Error intializing Jolt session pool");
	    }
	    System.out.println("initJoltPool(6).");
   }
}
