package exceptions;

public class CommonException extends RuntimeException {

    public CommonException(ErrorCodable errorCodable) {
        super(errorCodable.getError());
    }

    public CommonException(ErrorCodable errorCodable, Throwable cause) {
        super(errorCodable.getError(), cause);
    }

    public CommonException(Throwable cause) {
        super(cause);
    }
}
