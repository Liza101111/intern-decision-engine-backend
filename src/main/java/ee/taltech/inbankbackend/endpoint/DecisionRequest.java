package ee.taltech.inbankbackend.endpoint;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Holds the request data of the REST endpoint
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DecisionRequest {

    @NotBlank(message = "Personal code must not be blank")
    private String personalCode;

    @NotNull(message = "Loan amount must not be null")
    @Positive(message = "Loan amount must be positive")
    private Long loanAmount;

    @NotNull(message = "Loan period must not be null")
    @Positive(message = "Loan period must be positive")
    private int loanPeriod;
}
