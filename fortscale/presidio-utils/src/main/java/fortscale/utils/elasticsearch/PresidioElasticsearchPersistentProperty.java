package fortscale.utils.elasticsearch;

import fortscale.utils.elasticsearch.annotations.OneToMany;
import fortscale.utils.elasticsearch.annotations.OneToOne;
import fortscale.utils.elasticsearch.mapping.ElasticsearchAssociation;
import fortscale.utils.elasticsearch.mapping.ElasticsearchOneToManyAssociation;
import fortscale.utils.elasticsearch.mapping.ElasticsearchOneToOneAssociation;
import org.springframework.data.annotation.Reference;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchPersistentProperty;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class PresidioElasticsearchPersistentProperty extends SimpleElasticsearchPersistentProperty {

    public PresidioElasticsearchPersistentProperty(Field field, PropertyDescriptor propertyDescriptor, PersistentEntity<?, ElasticsearchPersistentProperty> owner, SimpleTypeHolder simpleTypeHolder) {
        super(field, propertyDescriptor, owner, simpleTypeHolder);
    }

    @Override
    protected Association<ElasticsearchPersistentProperty> createAssociation() {
        if (null != findAnnotation(OneToOne.class)) {
            return new ElasticsearchOneToOneAssociation(this, null);
        }

        if (null != findAnnotation(OneToMany.class)) {
            return new ElasticsearchOneToManyAssociation(this, null);
        }

        return new ElasticsearchAssociation(this, null);

    }

    @Override
    public boolean isAssociation() {
        return isAnnotationPresent(Reference.class);
    }
}
