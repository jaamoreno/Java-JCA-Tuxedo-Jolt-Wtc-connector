package weblogic.utils.collections;

import java.util.Enumeration;
import java.util.NoSuchElementException;


class HashtableEnumerator
    implements Enumeration
{
	boolean keys;
	int index;
	HashtableEntry table[];
	HashtableEntry entry;

    HashtableEnumerator(HashtableEntry ahashtableentry[], boolean flag)
    {
        table = ahashtableentry;
        keys = flag;
        index = ahashtableentry.length;
    }

    public boolean hasMoreElements()
    {
        if(entry != null)
            return true;
        while(index-- > 0)
            if((entry = table[index]) != null)
                return true;
        return false;
    }

    public Object nextElement()
    {
        if(entry == null)
            while(index-- > 0 && (entry = table[index]) == null) ;
        if(entry != null)
        {
            HashtableEntry hashtableentry = entry;
            entry = hashtableentry.next;
            return keys ? new Long(hashtableentry.key) : hashtableentry.value;
        } else
        {
            throw new NoSuchElementException("HashtableEnumerator");
        }
    }
}