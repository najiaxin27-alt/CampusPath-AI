package cn.campuspath.knowledge.api;

import org.springframework.ai.document.Document;

import java.util.Map;

public record KnowledgeSearchResult(
        String id,
        String content,
        Map<String, Object> metadata,
        Double score
) {

    public static KnowledgeSearchResult from(Document document) {
        return new KnowledgeSearchResult(
                document.getId(),
                document.getText(),
                document.getMetadata(),
                document.getScore()
        );
    }
}
