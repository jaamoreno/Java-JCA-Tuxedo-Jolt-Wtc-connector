package bea.jolt;


class JoltDefAttrs
{

    public static int getInteger(String s)
        throws NumberFormatException
    {
        if(s.charAt(0) != '0')
            return Integer.parseInt(s, 10);
        if(s.length() > 1 && s.charAt(1) == 'x')
            return Integer.parseInt(s.substring(2), 16);
        else
            return Integer.parseInt(s, 8);
    }

    JoltDefAttrs()
    {
    }

    public static final String SERVICE = "SVC";
    public static final String QUEUE = "QUEUE";
    public static final String EVENT = "EVENT";
    public static final String CONVERSATION = "CONV";
    public static final String VERSION = "vs";
    public static final String QSPACE = "qs";
    public static final String IBUFTYPE = "bt";
    public static final String OBUFTYPE = "BT";
    public static final String IVIEWNAME = "vn";
    public static final String OVIEWNAME = "VN";
    public static final String BOP = "bp";
    public static final String EOP = "ep";
    public static final String PARMNAME = "pn";
    public static final String PARMTYPE = "pt";
    public static final String PARMLEN = "pl";
    public static final String PARMDESC = "pd";
    public static final String PARMFID = "pf";
    public static final String PARMACC = "pa";
    public static final String PARMSTATUS = "ps";
    public static final String PARMOCC = "po";
    public static final String PARMFNAME = "fn";
    public static final String PARMFINDEX = "fi";
    public static final String BYTE = "byte";
    public static final String SHORT = "short";
    public static final String INTEGER = "integer";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BINARY = "carray";
    public static final String STRING = "string";
    public static final String AVLIST = "list";
    public static final String NOACC = "na";
    public static final String RDONLY = "rd";
    public static final String WRONLY = "wr";
    public static final String RDWR = "rw";
    public static final String MANDATORY = "man";
    public static final String OPTIONAL = "opt";
}
