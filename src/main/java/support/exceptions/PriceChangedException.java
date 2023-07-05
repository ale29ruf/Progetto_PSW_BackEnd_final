package support.exceptions;

public class PriceChangedException extends Exception{
    private int pid;

    public int getPid() {
        return pid;
    }

    public PriceChangedException(int pid) {
        super();
        this.pid = pid;
    }
}
