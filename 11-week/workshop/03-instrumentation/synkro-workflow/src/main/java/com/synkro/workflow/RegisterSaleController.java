package com.synkro.workflow;

import com.synkro.workflow.observability.RequestContext;
import com.synkro.workflow.observability.StructuredLogger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Minimal slice of POST /sagas/register-sale: it runs only step 1, validate-customer. */
@RestController
public class RegisterSaleController {

    public record StartSagaRequest(UUID customerId) {
    }

    private final CustomersClient customers;
    private final StructuredLogger logger;

    public RegisterSaleController(CustomersClient customers, StructuredLogger logger) {
        this.customers = customers;
        this.logger = logger;
    }

    @PostMapping("/sagas/register-sale")
    public ResponseEntity<Map<String, Object>> start(@RequestBody StartSagaRequest request) {
        if (request.customerId() == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "VALIDATION_ERROR",
                    "message", "customerId is required",
                    "traceId", RequestContext.get().correlationId()));
        }
        UUID sagaId = UUID.randomUUID();
        boolean valid = customers.isActive(request.customerId());

        Map<String, Object> saga = new LinkedHashMap<>();
        saga.put("sagaId", sagaId);
        saga.put("sagaType", "register-sale");
        saga.put("status", valid ? "RUNNING" : "COMPENSATED");
        saga.put("completedSteps", valid ? List.of("validate-customer") : List.of());
        if (!valid) {
            saga.put("failedStep", "validate-customer");
        }
        logger.info("saga.step.completed", "validate-customer finished", 0,
                Map.of("saga_id", sagaId.toString(), "saga_step", "validate-customer"));
        return ResponseEntity.status(201).body(saga);
    }
}
