package bea.jolt;


public class SBuffer
{

    public static int ID(int i, int j)
    {
        if(i == 0)
            return -1;
        else
            return j << 25 | i;
    }

    public static int TYPE(int i)
    {
        return i >>> 25;
    }

    public static int GETID(int i)
    {
        return i & 0x1ffffff;
    }

    public SBuffer()
    {
    }

    protected static final int SNBITS = 25;
    protected static final int SMASK = 0x1ffffff;
    public static final int SSHORT = 0;
    public static final int SINT = 1;
    public static final int SBYTE = 2;
    public static final int SFLOAT = 3;
    public static final int SDOUBLE = 4;
    public static final int SSTRING = 5;
    public static final int SBINARY = 6;
    public static final int SSTRUCT = 7;
    static final int UTFSTR = 0;
    static final int UNICODESTR = 1;
}
