package bea.jolt.pool;

public class ApplicationException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    private Result a_result;
    private String a_message;


    protected ApplicationException(Result result)
    {
        a_result = result;
    }

    protected ApplicationException(Result result, String message)
    {
        a_result = result;
        a_message = message;
    }

    public Result getResult()
    {
        return a_result;
    }

    public int getApplicationCode()
    {
        return a_result.getApplicationCode();
    }

    public String getDesc() {
		return a_message;
	}

	public void setDesc(String message) {
		this.a_message = message;
	}
}
