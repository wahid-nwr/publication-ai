package io.wahid.knowledge.domain.document;

import io.wahid.knowledge.domain.shared.BaseEntity;

public class Chunk extends BaseEntity {
    private String documentId;
    private String content;
    private Integer sequence;
}