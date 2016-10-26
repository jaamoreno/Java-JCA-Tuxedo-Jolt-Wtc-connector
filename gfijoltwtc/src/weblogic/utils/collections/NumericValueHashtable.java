package weblogic.utils.collections;

public class NumericValueHashtable extends NumericValueHashMap
{
	private static final long serialVersionUID = 1L;

    public NumericValueHashtable(int i, float f)
    {
        super(i, f);
    }

    public NumericValueHashtable(int i)
    {
        super(i);
    }

    public NumericValueHashtable()
    {
    }

    public synchronized boolean contains(long l)
    {
        return super.contains(l);
    }

    public synchronized boolean containsKey(Object obj)
    {
        return super.containsKey(obj);
    }

    public synchronized long get(Object obj)
    {
        return super.get(obj);
    }

    protected synchronized void rehash()
    {
        super.rehash();
    }

    public synchronized long put(Object obj, long l)
    {
        return super.put(obj, l);
    }

    public synchronized long remove(Object obj)
    {
        return super.remove(obj);
    }

    public synchronized void clear()
    {
        super.clear();
    }

    public synchronized Object clone()
    {
        return super.clone();
    }
}