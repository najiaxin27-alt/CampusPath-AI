package cn.campuspath.planning.application;

import cn.campuspath.planning.domain.CourseSelection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record PlanValidationRequest(
        @Positive(message = "学分上限必须大于 0")
        int maxCredits,

        @Valid
        @NotEmpty(message = "至少需要选择一门课程")
        List<CourseSelection> courses
) {
}