package ee.taltech.inbankbackend.service;

import com.github.vladislavgoltjajev.personalcode.locale.estonia.EstonianPersonalCodeValidator;
import ee.taltech.inbankbackend.ErrorHandler.ErrorResponse;
import ee.taltech.inbankbackend.ErrorHandler.ErrorResponseException;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.endpoint.Decision;
import ee.taltech.inbankbackend.endpoint.DecisionRequest;
import ee.taltech.inbankbackend.endpoint.DecisionResponse;

import org.springframework.stereotype.Service;

/**
 * A service class that provides a method for calculating an approved loan amount and period for a customer.
 * The loan amount is calculated based on the customer's credit modifier,
 * which is determined by the last four digits of their ID code.
 */
@Service
public class DecisionEngine {

    // Used to check for the validity of the presented ID code.
    private final EstonianPersonalCodeValidator validator = new EstonianPersonalCodeValidator();

    public DecisionResponse processDecisionRequest(DecisionRequest request) {
        int creditModifier;
        try {
            validateRequest(request);
            creditModifier = getCreditModifier(request.getPersonalCode());
            if(creditModifier == 0){
                throw new ErrorResponseException(ErrorResponse.NO_VALID_LOAN);
            }
            double creditScore = ((double) creditModifier / request.getLoanAmount()) * request.getLoanPeriod();

            Decision decision = determineDecision(creditScore);

            if (decision == Decision.APPROVED) {
                return (DecisionResponse) calculateApprovedLoanAmount(request, creditModifier);
            } else {
                return new DecisionResponse(Decision.UNAPPROVED, 0, 0);
            }
        } catch (ErrorResponseException ex) {
            throw new ErrorResponseException(ex.getErrorResponse());
        }

    }

    private void validateRequest(DecisionRequest request) {
        if (!validator.isValid(request.getPersonalCode())) {
            throw new ErrorResponseException(ErrorResponse.INVALID_PERSONAL_CODE);
        }
        if (request.getLoanAmount() < DecisionEngineConstants.MINIMUM_LOAN_AMOUNT ||
                request.getLoanAmount() > DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT) {
            throw new ErrorResponseException(ErrorResponse.INVALID_LOAN_AMOUNT);
        }
        if (request.getLoanPeriod() < DecisionEngineConstants.MINIMUM_LOAN_PERIOD ||
                request.getLoanPeriod() > DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
            throw new ErrorResponseException(ErrorResponse.INVALID_LOAN_PERIOD);
        }
    }

    /**
     * Calculates the credit modifier of the customer to according to the last four digits of their ID code.
     * Debt - 0000...2499
     * Segment 1 - 2500...4999
     * Segment 2 - 5000...7499
     * Segment 3 - 7500...9999
     *
     * @param personalCode ID code of the customer that made the request.
     * @return Segment to which the customer belongs.
     */
    private int getCreditModifier(String personalCode) {
        int segment = Integer.parseInt(personalCode.substring(personalCode.length() - 4));

        if (segment < 2500) {
            return 0;
        } else if (segment < 5000) {
            return DecisionEngineConstants.SEGMENT_1_CREDIT_MODIFIER;
        } else if (segment < 7500) {
            return DecisionEngineConstants.SEGMENT_2_CREDIT_MODIFIER;
        }

        return DecisionEngineConstants.SEGMENT_3_CREDIT_MODIFIER;
    }

    private Decision determineDecision( double creditScore)  {
        if (creditScore < 1) {
            return Decision.UNAPPROVED;
        } else {
            return Decision.APPROVED;
        }
    }

    private Object calculateApprovedLoanAmount(DecisionRequest request, int creditModifier) throws ErrorResponseException {
        int maxApprovedAmount = 0;
        int newLoanPeriod = request.getLoanPeriod();

        while (newLoanPeriod <= DecisionEngineConstants.MAXIMUM_LOAN_PERIOD) {
            maxApprovedAmount = Math.min(DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT, creditModifier * newLoanPeriod);
            if (maxApprovedAmount >= DecisionEngineConstants.MINIMUM_LOAN_AMOUNT) {
                return new DecisionResponse(Decision.APPROVED, maxApprovedAmount, newLoanPeriod);
            }
            newLoanPeriod++;
        }

        return new ErrorResponseException(ErrorResponse.NO_VALID_LOAN);
    }
}
