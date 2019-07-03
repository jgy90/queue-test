package exceptions;

public enum CommonErrorCode implements ErrorCodable {
    TOO_MANY_ARGUMENTS("too many arguments have been entered"),
    NOT_ENOUGH_ARGUMENTS("arguments are not enough"),
    UNKNOWN_OS_TYPE("OS type is unknown"),
    INVALID_FILE_PATH("file path what entered is invalid"),
    INVALID_NUMBER_OF_PARTITIONS("unavailable the number of partitions"),
    WORDS_FILE_NOT_EXISTS("words file is not exists"),
    RESULT_FOLDER_NOT_EXISTS("result folder is not exists"),

    PRODUCER_THREAD_INTERRUPTED("producer thread is interrupted while running"),
    WORDS_FILE_IO_ERROR("IO Exception is occurred while reading words file"),
    PUT_WORD_QUEUE_INTERRUPTED("main queue was interrupted while putting"),
    GET_WORD_QUEUE_INTERRUPTED("main queue was interrupted while getting"),
    PUT_WORD_SAVE_QUEUE_INTERRUPTED("save queue was interrupted while putting"),
    GET_WORD_SAVE_QUEUE_INTERRUPTED("save queue was interrupted while getting"),
    INTER_CONSUMER_THREAD_INTERRUPTED("intermediary consumer thread is interrupted while running"),
    SAVE_CONSUMER_THREAD_INTERRUPTED("save consumer thread is interrupted while running"),

    SAVE_FILE_OPEN_ERROR("error occurred while opening"),
    SAVE_FILE_FLUSH_ERROR("error occurred while flushing"),
    SAVE_FILE_CLOSE_ERROR("error occurred while closing"),
    SAVE_FILE_WRITE_ERROR("error occurred while writing"),

    INVALID_SETTING_VARIABLE("invalid setting variable"),

    ;

    String errorMessage;

    CommonErrorCode(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getError() {
        return this.errorMessage;
    }
}
