package bea.jolt.pool;

import java.util.Hashtable;
import java.util.Vector;

public class DataSet extends Hashtable
{

    public DataSet()
    {
        super(10);
        unrestricted = true;
    }

    public DataSet(int i)
    {
        super(i);
        unrestricted = true;
    }

    protected DataSet(int i, boolean flag)
    {
        super(i);
        unrestricted = true;
        unrestricted = flag;
    }

    public Object getValue(String s, int i, Object obj)
    {
    	Vector vector = (Vector) super.get(s);
		if (vector == null)
			return obj;
		try {
			return vector.elementAt(i);
		} catch (ArrayIndexOutOfBoundsException arrayindexoutofboundsexception) {
			return obj;
		}
    }

    public Object getValue(String s, Object obj)
    {
        return getValue(s, 0, obj);
    }

    public void setValue(String s, int i, Object obj)
    {
        Vector vector = (Vector)get(s);
        if(vector == null)
        {
            vector = new Vector(1, 5);
            put(s, vector);
        }
        if(i >= vector.size())
            vector.setSize(i + 1);
        vector.setElementAt(obj, i);
    }

    public void setValue(String s, Object obj)
    {
        setValue(s, 0, obj);
    }

    public int getCount(String s)
    {
        Vector vector = (Vector)super.get(s);
        if(vector == null)
            return 0;
        else
            return vector.size();
    }

    private static final long serialVersionUID = 1L;
    protected boolean unrestricted;
}
