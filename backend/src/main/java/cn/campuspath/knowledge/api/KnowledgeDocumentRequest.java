package cn.campuspath.knowledge.api;

import jakarta.validation.constraints.NotBlank;

public record KnowledgeDocumentRequest(

        @NotBlank(message = "知识标题不能为空")
        String title,

        @NotBlank(message = "知识内容不能为空")
        String content,

        @NotBlank(message = "知识分类不能为空")
        String category,

        @NotBlank(message = "知识来源不能为空")
        String source
) {
}