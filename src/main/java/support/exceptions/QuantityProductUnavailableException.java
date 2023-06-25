package support.exceptions;


public class QuantityProductUnavailableException extends Exception {

    public QuantityProductUnavailableException() {}

    public QuantityProductUnavailableException(String mes){
        super(mes);
    }

}
