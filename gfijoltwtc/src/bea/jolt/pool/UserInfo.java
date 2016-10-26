package bea.jolt.pool;

public class UserInfo
{
    String u_username;
    String u_userpwd;
    String u_userrole;
    String u_appwd;

	public void setUserName(String s)
    {
        u_username = s;
    }

    public void setUserPassword(String s)
    {
        u_userpwd = s;
    }

    public void setUserRole(String s)
    {
        u_userrole = s;
    }

    public void setAppPassword(String s)
    {
        u_appwd = s;
    }

    public UserInfo()
    {
    }
}
