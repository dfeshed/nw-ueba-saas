package fortscale.utils.elasticsearch;

import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class PresidioElasticsearchMappingContext extends SimpleElasticsearchMappingContext {

    @Override
    protected ElasticsearchPersistentProperty createPersistentProperty(Field field, PropertyDescriptor descriptor, SimpleElasticsearchPersistentEntity<?> owner, SimpleTypeHolder simpleTypeHolder) {
        return new PresidioElasticsearchPersistentProperty(field, descriptor, owner, simpleTypeHolder);
    }

    @Override
    protected <T> SimpleElasticsearchPersistentEntity<?> createPersistentEntity(TypeInformation<T> typeInformation) {
        return new PresidioElasticsearchPersistentEntity<>(this, typeInformation);
    }
}
