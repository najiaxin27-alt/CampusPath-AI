package cn.campuspath.planning.application;

import cn.campuspath.planning.domain.CourseSelection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class PlanValidationService {

    public PlanValidationResult validate(PlanValidationRequest request) {
        List<PlanViolation> violations = new ArrayList<>();

        int totalCredits = request.courses().stream()
                .mapToInt(CourseSelection::credits)
                .sum();

        if (totalCredits > request.maxCredits()) {
            violations.add(new PlanViolation(
                    "CREDIT_LIMIT_EXCEEDED",
                    "所选课程共 " + totalCredits
                            + " 学分，超过上限 " + request.maxCredits() + " 学分"
            ));
        }

        Set<String> courseCodes = new HashSet<>();
        Map<String, String> occupiedTimeSlots = new HashMap<>();

        for (CourseSelection course : request.courses()) {
            String normalizedCourseCode =
                    course.courseCode().trim().toUpperCase(Locale.ROOT);

            if (!courseCodes.add(normalizedCourseCode)) {
                violations.add(new PlanViolation(
                        "DUPLICATE_COURSE",
                        "课程 " + course.courseCode() + " 被重复选择"
                ));
            }

            String normalizedTimeSlot =
                    course.timeSlot().trim().toUpperCase(Locale.ROOT);

            String occupiedBy = occupiedTimeSlots.putIfAbsent(
                    normalizedTimeSlot,
                    course.courseCode()
            );

            if (occupiedBy != null
                    && !occupiedBy.equalsIgnoreCase(course.courseCode())) {
                violations.add(new PlanViolation(
                        "TIME_CONFLICT",
                        "课程 " + occupiedBy + " 与 " + course.courseCode()
                                + " 的上课时间冲突：" + course.timeSlot()
                ));
            }
        }

        return new PlanValidationResult(
                violations.isEmpty(),
                totalCredits,
                request.maxCredits(),
                List.copyOf(violations)
        );
    }
}