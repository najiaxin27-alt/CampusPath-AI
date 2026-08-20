package cn.campuspath.knowledge.api;

import cn.campuspath.knowledge.application.KnowledgeFileIngestionService;
import cn.campuspath.knowledge.application.KnowledgeIngestionService;
import cn.campuspath.knowledge.application.KnowledgeRagService;
import cn.campuspath.knowledge.application.KnowledgeSearchService;
import jakarta.validation.Valid;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(
        value = "/api/knowledge",
        produces = "application/json;charset=UTF-8"
)
public class KnowledgeQaController {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final KnowledgeIngestionService ingestionService;
    private final KnowledgeFileIngestionService fileIngestionService;
    private final KnowledgeSearchService searchService;
    private final KnowledgeRagService ragService;

    public KnowledgeQaController(
            ChatModel chatModel,
            EmbeddingModel embeddingModel,
            KnowledgeIngestionService ingestionService,
            KnowledgeFileIngestionService fileIngestionService,
            KnowledgeSearchService searchService,
            KnowledgeRagService ragService) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.ingestionService = ingestionService;
        this.fileIngestionService = fileIngestionService;
        this.searchService = searchService;
        this.ragService = ragService;
    }

    @GetMapping("/test")
    public Map<String, Object> test(
            @RequestParam(defaultValue = "请用一句话介绍人工智能") String question) {

        String answer = chatModel.call(question);
        float[] embedding = embeddingModel.embed(question);

        return Map.of(
                "question", question,
                "answer", answer,
                "embeddingDimensions", embedding.length
        );
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> ingest(
            @Valid @RequestBody KnowledgeDocumentRequest request) {

        String documentId = ingestionService.ingest(
                request.title(),
                request.content(),
                request.category(),
                request.source()
        );

        return Map.of(
                "documentId", documentId,
                "message", "校园知识入库成功"
        );
    }

    @PostMapping(
            value = "/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public KnowledgeFileUploadResponse uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam String category,
            @RequestParam String source) {

        try {
            KnowledgeFileIngestionService.FileIngestionResult result = fileIngestionService.ingest(
                    file.getOriginalFilename(),
                    file.getBytes(),
                    category,
                    source
            );

            return new KnowledgeFileUploadResponse(
                    result.fileName(),
                    result.chunkCount(),
                    result.documentIds(),
                    "校园知识文档入库成功"
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "无法读取上传文件", exception);
        }
    }

    @GetMapping("/search")
    public List<KnowledgeSearchResult> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") int topK) {

        return searchService.search(query, topK).stream()
                .map(KnowledgeSearchResult::from)
                .toList();
    }

    @PostMapping("/ask")
    public KnowledgeAnswerResponse ask(
            @Valid @RequestBody KnowledgeQuestionRequest request) {

        KnowledgeRagService.RagAnswer result = ragService.answer(
                request.question(),
                request.resolvedTopK()
        );

        List<KnowledgeSearchResult> sources = result.sources().stream()
                .map(KnowledgeSearchResult::from)
                .toList();

        return new KnowledgeAnswerResponse(
                request.question(),
                result.answer(),
                sources
        );
    }
}
