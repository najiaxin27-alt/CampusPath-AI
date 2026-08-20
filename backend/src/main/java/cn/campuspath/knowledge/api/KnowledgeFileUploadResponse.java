package cn.campuspath.knowledge.api;

import java.util.List;

public record KnowledgeFileUploadResponse(
        String fileName,
        int chunkCount,
        List<String> documentIds,
        String message
) {
}
