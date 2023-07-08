package support.exceptions;


public class QuantityProductUnavailableException extends Exception {

    private final String nomeProd;
    public String getName() {
        return nomeProd;
    }

    public QuantityProductUnavailableException(String name) {
        super();
        nomeProd = name;
    }

}
