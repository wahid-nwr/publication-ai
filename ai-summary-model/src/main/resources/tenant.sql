-- Add Row-Level Security (Recommended)
-- In PostgreSQL:

ALTER TABLE chunks
ENABLE ROW LEVEL SECURITY;

-- Policy:

CREATE POLICY tenant_isolation
ON chunks
USING (
    tenant_id =
    current_setting('app.tenant_id')::uuid
);

-- Then even accidental queries cannot leak data.