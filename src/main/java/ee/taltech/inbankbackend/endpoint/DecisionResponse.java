package ee.taltech.inbankbackend.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Holds the response data of the REST endpoint.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DecisionResponse {

    @Enumerated(EnumType.STRING)
    private Decision decision;

    private long approvedLoanAmount;

    private int approvedLoanPeriod;
}
