package support.exceptions;

public class InconsistencyCartException extends Exception{
    private String msg;

    public InconsistencyCartException(String msg){
        super();
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
