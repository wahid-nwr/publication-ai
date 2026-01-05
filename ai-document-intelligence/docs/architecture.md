                ┌─────────────┐
                │   REST API  │
                └──────┬──────┘
                       │
        ┌──────────────▼──────────────┐
        │        Query Engine          │
        │  (Retriever + Generator)    │
        └──────┬──────────────┬───────┘
               │              │
     ┌─────────▼───────┐  ┌───▼──────────┐
     │ Vector Database │  │ LLM Inference│
     │ (Qdrant)        │  │ (Ollama)     │
     └─────────┬───────┘  └──────────────┘
               │
     ┌─────────▼──────────────┐
     │ Document Processing     │
     │ - Chunking              │
     │ - Embeddings            │
     └─────────┬──────────────┘
               │
     ┌─────────▼──────────────┐
     │ Ingestion Layer         │
     │ CSV / PDF / MD / DOCX  │
     └────────────────────────┘

