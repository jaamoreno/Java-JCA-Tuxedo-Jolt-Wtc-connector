package weblogic.utils.collections;


class HashtableEntry implements Cloneable
{
    long key;
    Object value;
    HashtableEntry next;

    HashtableEntry()
    {
    }

    protected Object clone()
    {
        HashtableEntry hashtableentry = new HashtableEntry();
        hashtableentry.key = key;
        hashtableentry.value = value;
        hashtableentry.next = next == null ? null : (HashtableEntry)next.clone();
        return hashtableentry;
    }
}