package cn.campuspath.knowledge.application;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeSearchService {

    private static final double MIN_SIMILARITY = 0.5;

    private final VectorStore vectorStore;

    public KnowledgeSearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> search(String query, int topK) {
        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .similarityThreshold(MIN_SIMILARITY)
                .build();

        return vectorStore.similaritySearch(request);
    }
}
