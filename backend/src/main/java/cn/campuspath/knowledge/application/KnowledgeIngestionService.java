package cn.campuspath.knowledge.application;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class KnowledgeIngestionService {

    private final VectorStore vectorStore;

    public KnowledgeIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String ingest(
            String title,
            String content,
            String category,
            String source) {

        Document document = new Document(
                content,
                Map.of(
                        "title", title,
                        "category", category,
                        "source", source
                )
        );

        vectorStore.add(List.of(document));

        return document.getId();
    }
}