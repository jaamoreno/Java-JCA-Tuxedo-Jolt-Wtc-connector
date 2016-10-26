package bea.jolt.ejb.simpEjb;


import java.rmi.RemoteException;
import javax.ejb.EJBObject;

public interface InvierteRemote extends EJBObject
{
	public String invierte(String cadena) throws RemoteException;
}