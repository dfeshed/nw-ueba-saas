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

    @Override
    public String toString() {
        return "CollectorProperties{" +
                "schemaMappings=" + schemaMappings +
                '}';
    }
}