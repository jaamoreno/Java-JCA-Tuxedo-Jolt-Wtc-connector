/**	(c) 2003 BEA Systems, Inc. All Rights Reserved. **/
package bea.jolt.ejb.simpEjb;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

public class Client {

public Client()
{
}

public static void main(String[] args)
{
try{

	Properties props=new Properties();
	props.put(Context.INITIAL_CONTEXT_FACTORY,"com.ibm.websphere.naming.WsnInitialContextFactory");
	props.put(Context.PROVIDER_URL,"iiop://machine:9100");
	Context ctx=new InitialContext(props);
    Object o = ctx.lookup("ejb/simpEjb");
	InvierteHome myhome = (InvierteHome)PortableRemoteObject.narrow(o, InvierteHome.class);
	InvierteRemote h = myhome.create();
	System.out.println("Antes invocacion del EJB");
	System.out.println("["+ h.invierte("hola caracola")+ "]");
	System.out.println("Despues invocacion del EJB");

}
catch(Throwable t)
{
	//System.out.println(t.getCause().toString());
	//System.out.println(t.getMessage().toString());
	System.out.println("---------------------------");
	t.printStackTrace();

}

}
}