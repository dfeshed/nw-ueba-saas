package fortscale.domain.adapter;

import java.util.Set;

public class CollectorProperties {
    private Set<SchemaMapping> schemaMappings;

    public Set<SchemaMapping> getSchemaMappings() {
        return schemaMappings;
    }

    public void setSchemaMappings(Set<SchemaMapping> schemaMappings) {
        this.schemaMappings = schemaMappings;
    }

    public SchemaMapping getSchema(String schemaName) {
        for (SchemaMapping schemaMapping : schemaMappings) {
            if (schemaMapping.getSchemaName().equals(schemaName)) {
                return schemaMapping;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CollectorProperties{" +
                "schemaMappings=" + schemaMappings +
                '}';
    }
}