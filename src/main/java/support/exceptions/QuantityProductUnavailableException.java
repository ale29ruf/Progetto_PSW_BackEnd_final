package support.exceptions;


public class QuantityProductUnavailableException extends Exception {

    private int pid;
    public int getPid() {
        return pid;
    }

    public QuantityProductUnavailableException(int pid) {
        super();
        this.pid = pid;
    }

}
