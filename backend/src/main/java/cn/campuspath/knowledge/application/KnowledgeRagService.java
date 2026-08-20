package cn.campuspath.knowledge.application;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class KnowledgeRagService {

    private final KnowledgeSearchService searchService;
    private final ChatModel chatModel;

    public KnowledgeRagService(
            KnowledgeSearchService searchService,
            ChatModel chatModel) {
        this.searchService = searchService;
        this.chatModel = chatModel;
    }

    public RagAnswer answer(String question, int topK) {
        List<Document> sources = searchService.search(question, topK);

        if (sources.isEmpty()) {
            return new RagAnswer(
                    "当前知识库中没有足够信息回答这个问题。",
                    List.of()
            );
        }

        String context = IntStream.range(0, sources.size())
                .mapToObj(index -> "知识 %d：%s".formatted(
                        index + 1,
                        sources.get(index).getText()
                ))
                .reduce((first, second) -> first + System.lineSeparator() + second)
                .orElse("");

        String prompt = """
                你是 CampusPath AI 校园知识助手。
                请严格依据“校园知识”回答用户问题。
                如果校园知识不足以支持答案，请明确说明信息不足，不要编造校规、时间或办理流程。
                使用简洁、清晰的中文回答。

                校园知识：
                %s

                用户问题：
                %s
                """.formatted(context, question);

        String answer = chatModel.call(prompt);
        return new RagAnswer(answer, sources);
    }

    public record RagAnswer(
            String answer,
            List<Document> sources
    ) {
    }
}
