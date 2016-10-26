package weblogic.utils.collections;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;


public class NumericHashtable
    implements Cloneable, Serializable
{

    private transient HashtableEntry table[];
    private transient int count;
    private int threshold;
    private float loadFactor;

    private static final long serialVersionUID = 1L;

	public NumericHashtable(int i, float f)
    {
        if(i <= 0 || (double)f <= 0.0D)
        {
            throw new IllegalArgumentException();
        } else
        {
            loadFactor = f;
            table = new HashtableEntry[i];
            threshold = (int)((float)i * f);
            return;
        }
    }

    public NumericHashtable(int i)
    {
        this(i, 0.75F);
    }

    public NumericHashtable()
    {
        this(101, 0.75F);
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
        return new HashtableEnumerator(table, true);
    }

    public Enumeration elements()
    {
        return new HashtableEnumerator(table, false);
    }

    public synchronized boolean contains(Object obj)
    {
        if(obj == null)
            throw new NullPointerException();
        HashtableEntry ahashtableentry[] = table;
        for(int i = ahashtableentry.length; i-- > 0;)
        {
            HashtableEntry hashtableentry = ahashtableentry[i];
            while(hashtableentry != null)
            {
                if(hashtableentry.value.equals(obj))
                    return true;
                hashtableentry = hashtableentry.next;
            }
        }

        return false;
    }

    public synchronized boolean containsKey(long l)
    {
        HashtableEntry ahashtableentry[] = table;
        int i = (int)((l & 0x7fffffffL) % (long)ahashtableentry.length);
        for(HashtableEntry hashtableentry = ahashtableentry[i]; hashtableentry != null; hashtableentry = hashtableentry.next)
            if(hashtableentry.key == l)
                return true;

        return false;
    }

    public synchronized Object get(long l)
    {
        HashtableEntry ahashtableentry[] = table;
        int i = (int)((l & 0x7fffffffL) % (long)ahashtableentry.length);
        for(HashtableEntry hashtableentry = ahashtableentry[i]; hashtableentry != null; hashtableentry = hashtableentry.next)
            if(hashtableentry.key == l)
                return hashtableentry.value;

        return null;
    }

    protected synchronized void rehash()
    {
        int i = table.length;
        HashtableEntry ahashtableentry[] = table;
        int j = i * 2 + 1;
        HashtableEntry ahashtableentry1[] = new HashtableEntry[j];
        threshold = (int)((float)j * loadFactor);
        table = ahashtableentry1;
        for(int k = i; k-- > 0;)
        {
            HashtableEntry hashtableentry = ahashtableentry[k];
            while(hashtableentry != null)
            {
                HashtableEntry hashtableentry1 = hashtableentry;
                hashtableentry = hashtableentry.next;
                int l = (int)((hashtableentry1.key & 0x7fffffffL) % (long)j);
                hashtableentry1.next = ahashtableentry1[l];
                ahashtableentry1[l] = hashtableentry1;
            }
        }

    }

    public synchronized Object put(long l, Object obj)
    {
        if(obj == null)
            throw new NullPointerException();
        HashtableEntry ahashtableentry[] = table;
        int i = (int)((l & 0x7fffffffL) % (long)ahashtableentry.length);
        for(HashtableEntry hashtableentry = ahashtableentry[i]; hashtableentry != null; hashtableentry = hashtableentry.next)
            if(hashtableentry.key == l)
            {
                Object obj1 = hashtableentry.value;
                hashtableentry.value = obj;
                return obj1;
            }

        if(count >= threshold)
        {
            rehash();
            return put(l, obj);
        } else
        {
            HashtableEntry hashtableentry1 = new HashtableEntry();
            hashtableentry1.key = l;
            hashtableentry1.value = obj;
            hashtableentry1.next = ahashtableentry[i];
            ahashtableentry[i] = hashtableentry1;
            count++;
            return null;
        }
    }

    public synchronized Object remove(long l)
    {
        HashtableEntry ahashtableentry[] = table;
        int i = (int)((l & 0x7fffffffL) % (long)ahashtableentry.length);
        HashtableEntry hashtableentry = ahashtableentry[i];
        HashtableEntry hashtableentry1 = null;
        for(; hashtableentry != null; hashtableentry = hashtableentry.next)
        {
            if(hashtableentry.key == l)
            {
                if(hashtableentry1 != null)
                    hashtableentry1.next = hashtableentry.next;
                else
                    ahashtableentry[i] = hashtableentry.next;
                count--;
                return hashtableentry.value;
            }
            hashtableentry1 = hashtableentry;
        }

        return null;
    }

    public synchronized void clear()
    {
        HashtableEntry ahashtableentry[] = table;
        for(int i = ahashtableentry.length; --i >= 0;)
            ahashtableentry[i] = null;

        count = 0;
    }

    public synchronized Object clone()
    {
        NumericHashtable numerichashtable;

        try  /* JAM, se introduce try/catch para preservar el throw */
        {
            numerichashtable = (NumericHashtable)super.clone();
        }
        catch(CloneNotSupportedException cnse)
        {
            throw new InternalError();
        }

        numerichashtable.table = new HashtableEntry[table.length];
        for(int i = table.length; i-- > 0;)
            numerichashtable.table[i] = table[i] == null ? null : (HashtableEntry)table[i].clone();

        return numerichashtable;
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
            for(HashtableEntry hashtableentry = table[i]; hashtableentry != null; hashtableentry = hashtableentry.next)
            {
                objectoutputstream.writeLong(hashtableentry.key);
                objectoutputstream.writeObject(hashtableentry.value);
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
        table = new HashtableEntry[k];
        count = 0;
        for(; j > 0; j--)
        {
            long l = objectinputstream.readLong();
            Object obj = objectinputstream.readObject();
            put(l, obj);
        }

    }

    public static void main(String args[])
    {
        NumericHashtable numerichashtable = new NumericHashtable();
        Random random = new Random(System.currentTimeMillis());
        long al[] = new long[10];
        for(int i = 0; i < al.length; i++)
        {
            al[i] = random.nextLong();
            String s = String.valueOf(al[i]);
            numerichashtable.put(al[i], s);
            System.out.println("put: " + al[i] + ", '" + s + "'");
        }

        System.out.println("TABLE: \n" + numerichashtable);
        for(int j = 0; j < al.length; j++)
        {
            Object obj = numerichashtable.get(al[j]);
            if(obj == null)
            {
                System.err.println("not found: " + al[j]);
                continue;
            }
            if(!obj.equals(String.valueOf(al[j])))
                System.err.println(obj + "!=" + String.valueOf(al[j]));
            else
                System.out.println("OK: " + obj);
        }

    }


}