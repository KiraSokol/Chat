package error;

public class WrongCredentialException extends RuntimeException{
    public WrongCredentialException(String message) {
        super(message);
    }
}
