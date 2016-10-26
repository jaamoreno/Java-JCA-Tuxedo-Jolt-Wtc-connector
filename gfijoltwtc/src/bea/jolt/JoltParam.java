package bea.jolt;

import java.util.Enumeration;
import java.util.Hashtable;

public class JoltParam extends JoltDefAttrs
{

    public String getName()
    {
        return p_name;
    }

    public int getID()
    {
        return p_fid;
    }

    public int accessType()
    {
        return p_access;
    }

    public int getType()
    {
        return p_type;
    }

    public boolean isMandatory()
    {
        String s = (String)p_prop.get("ps");
        if(s == null)
            return false;
        else
            return "man".equals(s);
    }

    public int getMaxOccurrence()
    {
        String s = (String)p_prop.get("po");
        if(s == null)
            return 1;
        else
            return JoltDefAttrs.getInteger(s);
    }

    public int getMaxLength()
    {
        String s = (String)p_prop.get("pl");
        if(s == null)
            return 0;
        else
            return JoltDefAttrs.getInteger(s);
    }

    public String getProperty(String s)
    {
        return (String)p_prop.get(s);
    }

    public Enumeration getKeys()
    {
        return p_prop.keys();
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer("bp:");
        for(Enumeration enumeration = p_prop.keys(); enumeration.hasMoreElements(); stringbuffer.append(':'))
        {
            String s1 = (String)enumeration.nextElement();
            stringbuffer.append(s1);
            String s;
            if((s = (String)p_prop.get(s1)) != null)
                stringbuffer.append('=').append(s);
        }

        stringbuffer.append("ep:");
        return stringbuffer.toString();
    }

    public void addProperty(String s, String s1)
        throws NumberFormatException, DefinitionException
    {
        if(s.equals("pn"))
            p_name = s1;
        else
        if(s.equals("pf"))
            p_fid = JoltDefAttrs.getInteger(s1);
        else
        if(s.equals("pa"))
            p_access = findAccess(s1);
        else
        if(s.equals("fn"))
            p_fname = s1;
        else
        if(s.equals("fi"))
            p_findex = JoltDefAttrs.getInteger(s1);
        else
        if(s.equals("pt"))
            if(s1.equals("short"))
                p_type = 0;
            else
            if(s1.equals("integer"))
                p_type = 1;
            else
            if(s1.equals("byte"))
                p_type = 2;
            else
            if(s1.equals("float"))
                p_type = 3;
            else
            if(s1.equals("double"))
                p_type = 4;
            else
            if(s1.equals("string"))
                p_type = 5;
            else
            if(s1.equals("carray"))
                p_type = 6;
        p_prop.put(s, s1);
    }

    private int findAccess(String s)
        throws DefinitionException
    {
        if(s.equals("rd"))
            return 1;
        if(s.equals("wr"))
            return 2;
        if(s.equals("rw"))
            return 3;
        if(s.equals("na"))
            return 0;
        else
            throw new DefinitionException(s);
    }

    public JoltParam()
    {
        p_access = 0;
        p_prop = new Hashtable(13);
    }

    public static final int NOACCESS = 0;
    public static final int READONLY = 1;
    public static final int WRITEONLY = 2;
    public static final int READWRITE = 3;
    String p_name;
    String p_fname;
    int p_fid;
    int p_findex;
    int p_access;
    int p_type;
    Hashtable p_prop;
    JoltParam p_nextParam;
}
