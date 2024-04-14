package ee.taltech.inbankbackend.ErrorHandler;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Setter
@ResponseStatus(HttpStatus.OK)
public class ErrorResponseException extends RuntimeException{

    private final ErrorResponse errorResponse;

    public ErrorResponseException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.errorResponse = errorResponse;
    }

}
