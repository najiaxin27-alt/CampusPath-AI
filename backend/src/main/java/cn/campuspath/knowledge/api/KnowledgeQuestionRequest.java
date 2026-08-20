package cn.campuspath.knowledge.api;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record KnowledgeQuestionRequest(

        @NotBlank(message = "问题不能为空")
        String question,

        @Min(value = 1, message = "topK 不能小于 1")
        @Max(value = 10, message = "topK 不能大于 10")
        Integer topK
) {

    public int resolvedTopK() {
        return topK == null ? 3 : topK;
    }
}
