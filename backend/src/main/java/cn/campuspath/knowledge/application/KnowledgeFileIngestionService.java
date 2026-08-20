package cn.campuspath.knowledge.application;

import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class KnowledgeFileIngestionService {

    private static final int MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("txt", "md", "markdown");

    private final KnowledgeIngestionService ingestionService;

    public KnowledgeFileIngestionService(KnowledgeIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    public FileIngestionResult ingest(
            String originalFileName,
            byte[] fileBytes,
            String category,
            String source) {

        String fileName = sanitizeFileName(originalFileName);
        validateFile(fileName, fileBytes);

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("文档分类不能为空");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("文档来源不能为空");
        }

        String content = decodeUtf8(fileBytes);
        String title = fileName.substring(0, fileName.lastIndexOf('.'));

        KnowledgeIngestionService.DocumentIngestionResult result = ingestionService.ingestDocument(
                title,
                content,
                category.trim(),
                source.trim()
        );

        return new FileIngestionResult(
                fileName,
                result.chunkCount(),
                result.documentIds()
        );
    }

    private String sanitizeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("上传文件名不能为空");
        }

        String normalized = originalFileName.replace('\\', '/');
        return normalized.substring(normalized.lastIndexOf('/') + 1);
    }

    private void validateFile(String fileName, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (fileBytes.length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("上传文件不能超过 2 MB");
        }

        int extensionSeparator = fileName.lastIndexOf('.');
        if (extensionSeparator <= 0 || extensionSeparator == fileName.length() - 1) {
            throw new IllegalArgumentException("仅支持 .txt、.md、.markdown 文件");
        }

        String extension = fileName.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
        if (!SUPPORTED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("仅支持 .txt、.md、.markdown 文件");
        }
    }

    private String decodeUtf8(byte[] fileBytes) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(fileBytes))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new IllegalArgumentException("文件必须使用 UTF-8 编码", exception);
        }
    }

    public record FileIngestionResult(
            String fileName,
            int chunkCount,
            List<String> documentIds
    ) {
    }
}
