package cn.campuspath.knowledge.api;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeQaController {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;

    public KnowledgeQaController(
            ChatModel chatModel,
            EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
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
}