package io.wahid.publication.ai.processing;

import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;

import java.util.*;

public class SlidingWindowChunker implements Chunker {

    private final int maxTokens;
    private final int overlapTokens;

    private TextChunkConsumer downstream;

    private final Deque<String> buffer = new ArrayDeque<>();
    private int bufferedTokens = 0;

    private String currentDocumentId;
    private Map<String, Object> baseMetadata;

    public SlidingWindowChunker(int maxTokens, int overlapTokens) {
        this.maxTokens = maxTokens;
        this.overlapTokens = overlapTokens;
    }

    @Override
    public void setDownstream(TextChunkConsumer downstream) {
        this.downstream = downstream;
    }

    @Override
    public void accept(TextChunk input) throws Exception {
        if (currentDocumentId == null) {
            currentDocumentId = input.documentId();
            baseMetadata = input.metadata();
        }

        String text = input.text();
        int tokens = TokenEstimator.estimateTokens(text);

        if (tokens > maxTokens) {
            // Fallback: split large input aggressively
            splitLargeText(input);
            return;
        }

        buffer.addLast(text);
        bufferedTokens += tokens;

        if (bufferedTokens >= maxTokens) {
            emitChunk();
        }
    }

    private void emitChunk() throws Exception {
        StringBuilder sb = new StringBuilder();
        List<String> emitted = new ArrayList<>();

        int tokenCount = 0;
        for (String s : buffer) {
            int t = TokenEstimator.estimateTokens(s);
            if (tokenCount + t > maxTokens) break;
            sb.append(s).append('\n');
            tokenCount += t;
            emitted.add(s);
        }

        downstream.accept(new TextChunk(
                currentDocumentId,
                sb.toString().trim(),
                enrichMetadata()
        ));

        // Sliding window overlap
        buffer.clear();
        bufferedTokens = 0;

        int overlapCount = 0;
        ListIterator<String> it =
                emitted.listIterator(emitted.size());

        while (it.hasPrevious() && overlapCount < overlapTokens) {
            String s = it.previous();
            buffer.addFirst(s);
            overlapCount += TokenEstimator.estimateTokens(s);
            bufferedTokens += TokenEstimator.estimateTokens(s);
        }
    }

    private void splitLargeText(TextChunk input) throws Exception {
        String[] sentences = input.text().split("(?<=[.!?])\\s+");

        for (String sentence : sentences) {
            accept(new TextChunk(
                    input.documentId(),
                    sentence,
                    input.metadata()
            ));
        }
    }

    private Map<String, Object> enrichMetadata() {
        Map<String, Object> meta = new HashMap<>(baseMetadata);
        meta.put("chunked", true);
        meta.put("timestamp", System.currentTimeMillis());
        return meta;
    }

    @Override
    public void flush() throws Exception {
        if (!buffer.isEmpty()) {
            emitChunk();
        }
        buffer.clear();
        bufferedTokens = 0;
        currentDocumentId = null;
    }
}
