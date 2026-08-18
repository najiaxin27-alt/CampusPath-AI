package cn.campuspath.planning.api;

import cn.campuspath.planning.application.PlanValidationRequest;
import cn.campuspath.planning.application.PlanValidationResult;
import cn.campuspath.planning.application.PlanValidationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/plans")
public class PlanValidationController {

    private final PlanValidationService planValidationService;

    public PlanValidationController(
            PlanValidationService planValidationService
    ) {
        this.planValidationService = planValidationService;
    }

    @PostMapping("/validate")
    public PlanValidationResult validate(
            @Valid @RequestBody PlanValidationRequest request
    ) {
        return planValidationService.validate(request);
    }
}