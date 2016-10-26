package weblogic.utils.collections;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class NumericValueHashMapEnumerator
    implements Enumeration
{

    boolean keys;
    int index;
    NumericValueHashMapEntry table[];
    NumericValueHashMapEntry entry;

    NumericValueHashMapEnumerator(NumericValueHashMapEntry anumericvaluehashmapentry[], boolean flag)
    {
        table = anumericvaluehashmapentry;
        keys = flag;
        index = anumericvaluehashmapentry.length;
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
            NumericValueHashMapEntry numericvaluehashmapentry = entry;
            entry = numericvaluehashmapentry.next;
            return keys ? numericvaluehashmapentry.key : new Long(numericvaluehashmapentry.value);
        } else
        {
            throw new NoSuchElementException("HashtableEnumerator");
        }
    }
}
