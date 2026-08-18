package cn.campuspath.planning.application;

import java.util.List;

public record PlanValidationResult(
        boolean valid,
        int totalCredits,
        int maxCredits,
        List<PlanViolation> violations
) {
}