package bea.jolt.ejb.simpEjb;

import javax.ejb.*;
import java.rmi.RemoteException;


public interface InvierteHome extends EJBHome
{
	InvierteRemote create() throws RemoteException, CreateException;
}
