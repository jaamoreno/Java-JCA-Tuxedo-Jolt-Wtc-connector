package bea.jolt.pool.servlet;

import bea.jolt.pool.*;
import javax.servlet.http.HttpServletRequest;

public class ServletSessionPool extends SessionPool
{

    ServletSessionPool()
    {
    }

    ServletSessionPool(String as[], String as1[], int i, int j, UserInfo userinfo, boolean flag)
    {
        super(as, as1, i, j, userinfo, flag);
    }

    protected boolean useException()
    {
        return true;
    }

    public ServletResult call(String s, HttpServletRequest httpservletrequest)
        throws SessionPoolException, ApplicationException, ServiceException, TransactionException
    {
        return call(s, httpservletrequest, null);
    }

    public ServletResult call(String s, HttpServletRequest httpservletrequest, Transaction transaction)
        throws SessionPoolException, ApplicationException, ServiceException, TransactionException
    {
        ServletDataSet servletdataset = new ServletDataSet(30);
        if(httpservletrequest != null)
            servletdataset.importRequest(httpservletrequest);
        return (ServletResult)super.call(s, servletdataset, transaction);
    }

    protected DataSet newDataSet(int i, boolean flag)
    {
        return new ServletDataSet(i, flag);
    }

    protected Result newResult(int i)
    {
        return new ServletResult(i);
    }
}
