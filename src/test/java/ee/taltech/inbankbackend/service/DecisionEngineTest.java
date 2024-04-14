package ee.taltech.inbankbackend.service;

import ee.taltech.inbankbackend.ErrorHandler.ErrorResponseException;
import ee.taltech.inbankbackend.config.DecisionEngineConstants;
import ee.taltech.inbankbackend.endpoint.Decision;
import ee.taltech.inbankbackend.endpoint.DecisionRequest;
import ee.taltech.inbankbackend.endpoint.DecisionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DecisionEngineTest {

    @InjectMocks
    private DecisionEngine decisionEngine;

    private String debtorPersonalCode;
    private String segment1PersonalCode;
    private String segment2PersonalCode;
    private String segment3PersonalCode;

    @BeforeEach
    void setUp() {
        debtorPersonalCode = "37605030299";
        segment1PersonalCode = "50307172740";
        segment2PersonalCode = "38411266610";
        segment3PersonalCode = "35006069515";
    }

    @Test
    void testDebtorPersonalCode() {
        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(debtorPersonalCode, 4000L, 12)));
    }

    @Test
    void testSegment1PersonalCode() throws ErrorResponseException {
        DecisionResponse response = decisionEngine.processDecisionRequest(new DecisionRequest(segment1PersonalCode, 4000L, 12));
        assertEquals(Decision.UNAPPROVED, response.getDecision());
        assertEquals(0, response.getApprovedLoanAmount());
        assertEquals(0, response.getApprovedLoanPeriod());
    }

    @Test
    void testSegment2PersonalCode() throws ErrorResponseException {
        DecisionResponse response = decisionEngine.processDecisionRequest(new DecisionRequest(segment2PersonalCode, 4000L, 12));
        assertEquals(Decision.UNAPPROVED, response.getDecision());
        assertEquals(0, response.getApprovedLoanAmount());
        assertEquals(0, response.getApprovedLoanPeriod());
    }


    @Test
    void testInvalidAge()  {
        DecisionRequest request= new DecisionRequest(segment3PersonalCode, 4000L, 12);
        assertThrows(ErrorResponseException.class, () -> decisionEngine.processDecisionRequest(request));
    }

    @Test
    void testInvalidPersonalCode() {
        String invalidPersonalCode = "12345678901";
        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(invalidPersonalCode, 4000L, 12)));
    }

    @Test
    void testInvalidLoanAmount() {
        Long tooLowLoanAmount = DecisionEngineConstants.MINIMUM_LOAN_AMOUNT - 1L;
        Long tooHighLoanAmount = DecisionEngineConstants.MAXIMUM_LOAN_AMOUNT + 1L;

        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(segment1PersonalCode, tooLowLoanAmount, 12)));

        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(segment1PersonalCode, tooHighLoanAmount, 12)));
    }

    @Test
    void testInvalidLoanPeriod() {
        int tooShortLoanPeriod = DecisionEngineConstants.MINIMUM_LOAN_PERIOD - 1;
        int tooLongLoanPeriod = DecisionEngineConstants.MAXIMUM_LOAN_PERIOD + 1;

        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(segment1PersonalCode, 4000L, tooShortLoanPeriod)));

        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(segment1PersonalCode, 4000L, tooLongLoanPeriod)));

    }

    @Test
    void testFindSuitableLoanPeriod() throws ErrorResponseException {
        DecisionResponse response = decisionEngine.processDecisionRequest(new DecisionRequest(segment2PersonalCode, 2000L, 12));
        assertEquals(3600, response.getApprovedLoanAmount());
        assertEquals(12, response.getApprovedLoanPeriod());
    }

    @Test
    void testNoValidLoanFound() {
        assertThrows(ErrorResponseException.class,
                () -> decisionEngine.processDecisionRequest(new DecisionRequest(debtorPersonalCode, 10000L, 60)));
    }

}

