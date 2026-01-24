package io.wahid.publication.ai.processing;

import io.wahid.publication.ai.ingestion.AbstractPipelineStage;
import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;

import java.util.*;

/**
 * Sliding window chunker with:
 * - soft token limit
 * - hard char limit
 * - non-recursive splitting
 * - no duplicate emission
 */
public final class SlidingWindowChunker
        extends AbstractPipelineStage
        implements TextChunkConsumer {

    private final int maxTokens;
    private final int overlapTokens;
    private final int maxChars;

    private final Deque<String> buffer = new ArrayDeque<>();
    private int bufferedTokens = 0;
    private int bufferedChars = 0;

    private String documentId;
    private Map<String, Object> baseMetadata;


    public SlidingWindowChunker(int maxTokens, int overlapTokens, int maxChars) {
        this.maxTokens = maxTokens;
        this.overlapTokens = overlapTokens;
        this.maxChars = maxChars;
    }

    public SlidingWindowChunker(int maxTokens, int overlapTokens) {
        this(maxTokens, overlapTokens, 1200);
    }

    @Override
    public void accept(TextChunk input) throws Exception {
        if (documentId == null) {
            documentId = input.documentId();
            baseMetadata = input.metadata();
        }

        // Split input into safe pieces FIRST (stateless)
        for (String piece : splitSafely(input.text())) {
            append(piece);
        }
    }

    private void append(String text) throws Exception {
        int tokens = TokenEstimator.estimateTokens(text);
        int chars = text.length();

        if (chars > maxChars) {
            // hard cut fallback
            for (int i = 0; i < text.length(); i += maxChars) {
                append(text.substring(i, Math.min(i + maxChars, text.length())));
            }
            return;
        }

        if (bufferedTokens + tokens > maxTokens
                || bufferedChars + chars > maxChars) {
            emit();
        }

        buffer.addLast(text);
        bufferedTokens += tokens;
        bufferedChars += chars;
    }

    private void emit() throws Exception {
        if (buffer.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        List<String> emitted = new ArrayList<>();

        int t = 0;
        int c = 0;

        for (String s : buffer) {
            int ts = TokenEstimator.estimateTokens(s);
            int cs = s.length();

            if (t + ts > maxTokens || c + cs > maxChars) break;

            sb.append(s).append('\n');
            t += ts;
            c += cs;
            emitted.add(s);
        }

        downstream.accept(new TextChunk(
                documentId,
                sb.toString().trim(),
                enrichMetadata()
        ));

        rebuildOverlap(emitted);
    }

    private void rebuildOverlap(List<String> emitted) {
        buffer.clear();
        bufferedTokens = 0;
        bufferedChars = 0;

        int overlap = 0;

        ListIterator<String> it = emitted.listIterator(emitted.size());
        while (it.hasPrevious() && overlap < overlapTokens) {
            String s = it.previous();
            int ts = TokenEstimator.estimateTokens(s);
            int cs = s.length();

            if (bufferedChars + cs > maxChars) break;

            buffer.addFirst(s);
            bufferedTokens += ts;
            bufferedChars += cs;
            overlap += ts;
        }
    }

    private List<String> splitSafely(String text) {
        // sentence-aware split, no recursion
        return Arrays.asList(text.split("(?<=[.!?])\\s+"));
    }

    private Map<String, Object> enrichMetadata() {
        Map<String, Object> meta = new HashMap<>(baseMetadata);
        meta.put("chunked", true);
        meta.put("timestamp", System.currentTimeMillis());
        return meta;
    }

    @Override
    public void flush() throws Exception {
        emit();
        buffer.clear();
        bufferedTokens = 0;
        bufferedChars = 0;
        documentId = null;
        baseMetadata = null;
    }
}
