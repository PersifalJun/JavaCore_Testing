package testing.exceptions;

public class OrderSaveFailedException extends RuntimeException {
    public OrderSaveFailedException(String message) {
        super(message);
    }

}
