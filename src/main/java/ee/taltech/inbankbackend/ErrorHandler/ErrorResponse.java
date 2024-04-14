package ee.taltech.inbankbackend.ErrorHandler;

public enum ErrorResponse {

    INVALID_PERSONAL_CODE("Invalid personal ID code!"),
    INVALID_LOAN_AMOUNT("Invalid loan amount!"),
    INVALID_LOAN_PERIOD("Invalid loan period!"),

    INTERNAL_SERVER_ERROR("Internal server error"),

    NO_VALID_LOAN("No valid loan found!");

    private final String message;

    ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
