package bea.jolt;

import java.util.Enumeration;

public class JoltSessionAttributes
{

    public JoltSessionAttributes()
    {
    }

    public void clear()
    {
    }

    public byte getByteDef(String s, byte byte0)
    {
        return byte0;
    }

    public short getShortDef(String s, short word0)
    {
        return word0;
    }

    public int getIntDef(String s, int i)
    {
        return i;
    }

    public float getFloatDef(String s, float f)
    {
        return f;
    }

    public double getDoubleDef(String s, double d)
    {
        return d;
    }

    public String getStringDef(String s, String s1)
    {
        return s1;
    }

    public byte[] getBytesDef(String s, byte abyte0[])
    {
        return abyte0;
    }

    public void setByte(String s1, byte byte1)
    {
    }

    public void setShort(String s1, short word1)
    {
    }

    public void setInt(String s1, int j)
    {
    }

    public void setFloat(String s1, float f1)
    {
    }

    public void setDouble(String s1, double d1)
    {
    }

    public void setString(String s2, String s3)
    {
    }

    public void setBytes(String s1, byte abyte1[], int j)
    {
    }

    public Enumeration getKeys()
    {
        return null;
    }

    public int checkAuthenticationLevel()
        throws SessionException
    {
        return 0;
    }

    int getDomainInfo(boolean flag)
        throws SessionException
    {
        return 0;
    }

    public String JoltClientVersion()
    {
        return null;
    }

    public static final String APPADDRESS = "APPADDRESS";
    public static final String IDLETIMEOUT = "IDLETIMEOUT";
    public static final String SENDTIMEOUT = "SENDTIMEOUT";
    public static final String RECVTIMEOUT = "RECVTIMEOUT";
    public static final String JOLTVERSION = "JOLTVERSION";
}
