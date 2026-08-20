package cn.campuspath.knowledge.api;

import java.util.List;

public record KnowledgeAnswerResponse(
        String question,
        String answer,
        List<KnowledgeSearchResult> sources
) {
}
