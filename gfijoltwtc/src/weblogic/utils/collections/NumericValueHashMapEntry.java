package weblogic.utils.collections;

class NumericValueHashMapEntry
    implements Cloneable
{

    Object key;
    long value;
    NumericValueHashMapEntry next;

    NumericValueHashMapEntry()
    {
    }

    protected Object clone()
    {
        NumericValueHashMapEntry numericvaluehashmapentry = new NumericValueHashMapEntry();
        numericvaluehashmapentry.key = key;
        numericvaluehashmapentry.value = value;
        numericvaluehashmapentry.next = next == null ? null : (NumericValueHashMapEntry)next.clone();
        return numericvaluehashmapentry;
    }
}