package cn.campuspath.planning.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CourseSelection(
        @NotBlank(message = "课程编号不能为空")
        String courseCode,

        @NotBlank(message = "课程名称不能为空")
        String courseName,

        @Positive(message = "课程学分必须大于 0")
        int credits,

        @NotBlank(message = "上课时间不能为空")
        String timeSlot
) {
}