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

    public CommonException(CommonErrorCode errorCodable, String s, int value) {
        super(errorCodable.getError() + " : " + s + ": " + value);
    }

    public CommonException(CommonErrorCode errorCodable, String s, long value) {
        super(errorCodable.getError() + " : " + s + ": " + value);
    }
}
