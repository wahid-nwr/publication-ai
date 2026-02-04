                    ┌───────────────┐
User Question ───▶  │ Intent Router │
                    └───────┬───────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
    Descriptive         Numeric / Graph     Comparison
         │                  │                  │
    Qdrant + LLM          Neo4j              Neo4j
         │                  │                  │
         └──────────────▶  LLM (Explain)  ◀────┘
