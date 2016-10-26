
package weblogic.utils.collections;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;


public class NumericValueHashMap
    implements Cloneable, Serializable
{
	private transient NumericValueHashMapEntry table[];
	private transient int count;
	private int threshold;
	private float loadFactor;
	private static final long serialVersionUID = 1L;

	public NumericValueHashMap(int i, float f)
    {
        if(i <= 0 || (double)f <= 0.0D)
        {
            throw new IllegalArgumentException();
        } else
        {
            loadFactor = f;
            table = new NumericValueHashMapEntry[i];
            threshold = (int)((float)i * f);
            return;
        }
    }

    public NumericValueHashMap(int i)
    {
        this(i, 0.75F);
    }

    public NumericValueHashMap()
    {
        this(10, 3F);
    }

    public int size()
    {
        return count;
    }

    public boolean isEmpty()
    {
        return count == 0;
    }

    public Enumeration keys()
    {
        return new NumericValueHashMapEnumerator(table, true);
    }

    public Enumeration elements()
    {
        return new NumericValueHashMapEnumerator(table, false);
    }

    public boolean contains(long l)
    {
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        for(int i = anumericvaluehashmapentry.length; i-- > 0;)
        {
            NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[i];
            while(numericvaluehashmapentry != null)
            {
                if(numericvaluehashmapentry.value == l)
                    return true;
                numericvaluehashmapentry = numericvaluehashmapentry.next;
            }
        }

        return false;
    }

    public boolean containsKey(Object obj)
    {
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        int i = (obj.hashCode() & 0x7fffffff) % anumericvaluehashmapentry.length;
        for(NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[i]; numericvaluehashmapentry != null; numericvaluehashmapentry = numericvaluehashmapentry.next)
            if(numericvaluehashmapentry.key.equals(obj))
                return true;

        return false;
    }

    public long get(Object obj)
    {
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        int i = (obj.hashCode() & 0x7fffffff) % anumericvaluehashmapentry.length;
        for(NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[i]; numericvaluehashmapentry != null; numericvaluehashmapentry = numericvaluehashmapentry.next)
            if(numericvaluehashmapentry.key.equals(obj))
                return numericvaluehashmapentry.value;

        return 0L;
    }

    protected void rehash()
    {
        int i = table.length;
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        int j = i * 2 + 1;
        NumericValueHashMapEntry anumericvaluehashmapentry1[] = new NumericValueHashMapEntry[j];
        threshold = (int)((float)j * loadFactor);
        table = anumericvaluehashmapentry1;
        for(int k = i; k-- > 0;)
        {
            NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[k];
            while(numericvaluehashmapentry != null)
            {
                NumericValueHashMapEntry numericvaluehashmapentry1 = numericvaluehashmapentry;
                numericvaluehashmapentry = numericvaluehashmapentry.next;
                int l = (numericvaluehashmapentry1.key.hashCode() & 0x7fffffff) % j;
                numericvaluehashmapentry1.next = anumericvaluehashmapentry1[l];
                anumericvaluehashmapentry1[l] = numericvaluehashmapentry1;
            }
        }

    }

    public long put(Object obj, long l)
    {
        if(obj == null)
            throw new NullPointerException();
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        int i = (obj.hashCode() & 0x7fffffff) % anumericvaluehashmapentry.length;
        for(NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[i]; numericvaluehashmapentry != null; numericvaluehashmapentry = numericvaluehashmapentry.next)
            if(numericvaluehashmapentry.key.equals(obj))
            {
                long l1 = numericvaluehashmapentry.value;
                numericvaluehashmapentry.value = l;
                return l1;
            }

        if(count >= threshold)
        {
            rehash();
            return put(obj, l);
        } else
        {
            NumericValueHashMapEntry numericvaluehashmapentry1 = new NumericValueHashMapEntry();
            numericvaluehashmapentry1.key = obj;
            numericvaluehashmapentry1.value = l;
            numericvaluehashmapentry1.next = anumericvaluehashmapentry[i];
            anumericvaluehashmapentry[i] = numericvaluehashmapentry1;
            count++;
            return 0L;
        }
    }

    public long remove(Object obj)
    {
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        int i = (obj.hashCode() & 0x7fffffff) % anumericvaluehashmapentry.length;
        NumericValueHashMapEntry numericvaluehashmapentry = anumericvaluehashmapentry[i];
        NumericValueHashMapEntry numericvaluehashmapentry1 = null;
        for(; numericvaluehashmapentry != null; numericvaluehashmapentry = numericvaluehashmapentry.next)
        {
            if(numericvaluehashmapentry.key.equals(obj))
            {
                if(numericvaluehashmapentry1 != null)
                    numericvaluehashmapentry1.next = numericvaluehashmapentry.next;
                else
                    anumericvaluehashmapentry[i] = numericvaluehashmapentry.next;
                count--;
                return numericvaluehashmapentry.value;
            }
            numericvaluehashmapentry1 = numericvaluehashmapentry;
        }

        return 0L;
    }

    public void clear()
    {
        NumericValueHashMapEntry anumericvaluehashmapentry[] = table;
        for(int i = anumericvaluehashmapentry.length; --i >= 0;)
            anumericvaluehashmapentry[i] = null;

        count = 0;
    }

    public Object clone()
    {
        NumericValueHashMap numericvaluehashmap;
        try  /* JAM, se introduce try/catch para preservar el throw */
        {
            numericvaluehashmap = (NumericValueHashMap)super.clone();
        }
        catch(CloneNotSupportedException cnse)
        {
            throw new InternalError();
        }

        numericvaluehashmap.table = new NumericValueHashMapEntry[table.length];
        for(int i = table.length; i-- > 0;)
            numericvaluehashmap.table[i] = table[i] == null ? null : (NumericValueHashMapEntry)table[i].clone();

        return numericvaluehashmap;
    }

    public String toString()
    {
        int i = size() - 1;
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = keys();
        Enumeration enumeration1 = elements();
        stringbuffer.append("{");
        for(int j = 0; j <= i; j++)
        {
            String s = enumeration.nextElement().toString();
            String s1 = enumeration1.nextElement().toString();
            stringbuffer.append(s + "=" + s1);
            if(j < i)
                stringbuffer.append(", ");
        }

        stringbuffer.append("}");
        return stringbuffer.toString();
    }

    private void writeObject(ObjectOutputStream objectoutputstream)
        throws IOException
    {
        objectoutputstream.defaultWriteObject();
        objectoutputstream.writeInt(table.length);
        objectoutputstream.writeInt(count);
        for(int i = table.length - 1; i >= 0; i--)
        {
            for(NumericValueHashMapEntry numericvaluehashmapentry = table[i]; numericvaluehashmapentry != null; numericvaluehashmapentry = numericvaluehashmapentry.next)
            {
                objectoutputstream.writeObject(numericvaluehashmapentry.key);
                objectoutputstream.writeLong(numericvaluehashmapentry.value);
            }

        }

    }

    private void readObject(ObjectInputStream objectinputstream)
        throws IOException, ClassNotFoundException
    {
        objectinputstream.defaultReadObject();
        int i = objectinputstream.readInt();
        int j = objectinputstream.readInt();
        int k = (int)((float)j * loadFactor) + j / 20 + 3;
        if(k > j && (k & 1) == 0)
            k--;
        if(i > 0 && k > i)
            k = i;
        table = new NumericValueHashMapEntry[k];
        count = 0;
        for(; j > 0; j--)
        {
            Object obj = objectinputstream.readObject();
            long l = objectinputstream.readLong();
            put(obj, l);
        }

    }

    public static void main(String args[])
    {
        NumericValueHashMap numericvaluehashmap = new NumericValueHashMap();
        Random random = new Random(System.currentTimeMillis());
        long al[] = new long[10];
        for(int i = 0; i < al.length; i++)
        {
            al[i] = random.nextLong();
            String s = String.valueOf(al[i]);
            numericvaluehashmap.put(s, al[i]);
            System.out.println("put: '" + s + "', '" + al[i] + "'");
        }

        System.out.println("TABLE: \n" + numericvaluehashmap);
        for(int j = 0; j < al.length; j++)
        {
            long l = numericvaluehashmap.get(String.valueOf(al[j]));
            if(l == 0L)
            {
                System.err.println("not found: " + al[j]);
                continue;
            }
            if(l != al[j])
                System.err.println(l + "!=" + String.valueOf(al[j]));
            else
                System.out.println("OK: " + l);
        }

    }
}