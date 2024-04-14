package ee.taltech.inbankbackend.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.inbankbackend.ErrorHandler.ErrorResponse;
import ee.taltech.inbankbackend.ErrorHandler.ErrorResponseException;
import ee.taltech.inbankbackend.service.DecisionEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class holds integration tests for the DecisionEngineController endpoint.
 */

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(DecisionEngineController.class)
public class DecisionEngineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DecisionEngine decisionEngine;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
    }

    /**
     * This method tests the /loan/decision endpoint with valid inputs.
     */
    @Test
    public void givenValidRequest_whenRequestDecision_thenReturnsExpectedResponse() throws Exception {

        DecisionRequest request = new DecisionRequest("35006069515",4000L,12);
        DecisionResponse expectedResponse = new DecisionResponse(Decision.APPROVED,10000,12);

        when(decisionEngine.processDecisionRequest(request)).thenReturn(expectedResponse);

        mockMvc.perform(post("/loan/decision")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("APPROVED"))
                .andExpect(jsonPath("$.loanAmount").value(10000))
                .andExpect(jsonPath("$.loanPeriod").value(12));
    }

    @Test
    public void givenInvalidPersonalCode_whenRequestDecision_thenReturnsBadRequest() throws Exception {

        String invalidPersonalCode = "invalid";
        DecisionRequest request = new DecisionRequest(invalidPersonalCode, 4000L, 12);
        ErrorResponse expectedErrorResponse = ErrorResponse.INVALID_PERSONAL_CODE;


        when(decisionEngine.processDecisionRequest(request)).thenThrow(new ErrorResponseException(expectedErrorResponse));


        mockMvc.perform(post("/loan/decision")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedErrorResponse.getMessage()));
    }

}
