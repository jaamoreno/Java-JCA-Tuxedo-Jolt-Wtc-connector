package bea.jolt;

import java.util.Enumeration;

class ParamEnumeration
    implements Enumeration
{

    ParamEnumeration(JoltParam joltparam)
    {
        p_next = joltparam;
    }

    public boolean hasMoreElements()
    {
        return p_next != null;
    }

    public Object nextElement()
    {
        JoltParam joltparam = p_next;
        p_next = p_next.p_nextParam;
        return joltparam;
    }

    private JoltParam p_next;
}
