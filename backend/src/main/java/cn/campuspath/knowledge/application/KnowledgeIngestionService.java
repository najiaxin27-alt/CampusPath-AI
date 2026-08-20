package cn.campuspath.knowledge.application;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
public class KnowledgeIngestionService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;

    public KnowledgeIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.textSplitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(100)
                .withKeepSeparator(true)
                .build();
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

    public DocumentIngestionResult ingestDocument(
            String title,
            String content,
            String category,
            String source) {

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("文档内容不能为空");
        }

        Document sourceDocument = new Document(
                content,
                Map.of(
                        "title", title,
                        "category", category,
                        "source", source
                )
        );

        List<Document> splitDocuments = textSplitter.split(sourceDocument);
        int chunkCount = splitDocuments.size();

        List<Document> chunks = IntStream.range(0, chunkCount)
                .mapToObj(index -> splitDocuments.get(index).mutate()
                        .metadata("chunkIndex", index)
                        .metadata("chunkCount", chunkCount)
                        .build())
                .toList();

        vectorStore.add(chunks);

        return new DocumentIngestionResult(
                chunkCount,
                chunks.stream().map(Document::getId).toList()
        );
    }

    public record DocumentIngestionResult(
            int chunkCount,
            List<String> documentIds
    ) {
    }
}
