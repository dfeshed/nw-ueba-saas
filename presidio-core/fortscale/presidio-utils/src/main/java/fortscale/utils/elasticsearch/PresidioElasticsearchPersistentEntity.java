package fortscale.utils.elasticsearch;

import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentEntity;
import org.springframework.data.util.TypeInformation;

public class PresidioElasticsearchPersistentEntity<T> extends SimpleElasticsearchPersistentEntity<T> {

    SimpleElasticsearchMappingContext context;

    public PresidioElasticsearchPersistentEntity(SimpleElasticsearchMappingContext context, TypeInformation<T> typeInformation) {
        super(typeInformation);
        this.context = context;
    }

    public SimpleElasticsearchMappingContext getContext() {
        return context;
    }
}
