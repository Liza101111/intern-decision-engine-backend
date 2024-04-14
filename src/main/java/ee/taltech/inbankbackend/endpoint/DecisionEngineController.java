package ee.taltech.inbankbackend.endpoint;

import ee.taltech.inbankbackend.ErrorHandler.ErrorResponseException;
import ee.taltech.inbankbackend.service.DecisionEngine;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loan")
@CrossOrigin
public class DecisionEngineController {

    private final DecisionEngine decisionEngine;

    @Autowired
    public DecisionEngineController(DecisionEngine decisionEngine) {
        this.decisionEngine = decisionEngine;
    }

    @PostMapping("/decision")
    public ResponseEntity<?> processDecisionRequest(@Valid @RequestBody DecisionRequest request) {
        try {
            DecisionResponse response = decisionEngine.processDecisionRequest(request);
            return ResponseEntity.ok(response);
        } catch (ErrorResponseException ex) {
            return ResponseEntity.badRequest().body(ex.getErrorResponse());
        }
    }
}
