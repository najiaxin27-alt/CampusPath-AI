package cn.campuspath.planning.application;

import cn.campuspath.planning.domain.CourseSelection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanValidationServiceTest {

    private final PlanValidationService service = new PlanValidationService();

    @Test
    void shouldAcceptValidPlan() {
        PlanValidationRequest request = new PlanValidationRequest(
                8,
                List.of(
                        new CourseSelection("CS101", "Java", 3, "MON-1-2"),
                        new CourseSelection("AI201", "AI", 2, "TUE-3-4")
                )
        );

        PlanValidationResult result = service.validate(request);

        assertTrue(result.valid());
        assertEquals(5, result.totalCredits());
        assertEquals(8, result.maxCredits());
        assertTrue(result.violations().isEmpty());
    }

    @Test
    void shouldDetectCreditLimitAndTimeConflict() {
        PlanValidationRequest request = new PlanValidationRequest(
                5,
                List.of(
                        new CourseSelection("CS101", "Java", 3, "MON-1-2"),
                        new CourseSelection("AI201", "AI", 3, "MON-1-2")
                )
        );

        PlanValidationResult result = service.validate(request);
        List<String> violationCodes = result.violations().stream()
                .map(PlanViolation::code)
                .toList();

        assertFalse(result.valid());
        assertEquals(6, result.totalCredits());
        assertTrue(violationCodes.contains("CREDIT_LIMIT_EXCEEDED"));
        assertTrue(violationCodes.contains("TIME_CONFLICT"));
    }

    @Test
    void shouldDetectDuplicateCourseIgnoringCaseAndSpaces() {
        PlanValidationRequest request = new PlanValidationRequest(
                10,
                List.of(
                        new CourseSelection("CS101", "Java", 3, "MON-1-2"),
                        new CourseSelection(" cs101 ", "Java", 3, "TUE-3-4")
                )
        );

        PlanValidationResult result = service.validate(request);

        assertFalse(result.valid());
        assertEquals(1, result.violations().size());
        assertEquals("DUPLICATE_COURSE", result.violations().get(0).code());
    }
}
