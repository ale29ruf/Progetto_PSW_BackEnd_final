package support.exceptions;

public class PriceChangedException extends Exception{
    private final String nameProd;

    public String getName() {
        return nameProd;
    }

    public PriceChangedException(String nameProd) {
        super();
        this.nameProd = nameProd;
    }
}
