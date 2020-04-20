package fortscale.utils.elasticsearch;


import fortscale.utils.elasticsearch.mapping.AssociationsResolver;
import fortscale.utils.elasticsearch.mapping.ElasticsearchAssociation;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.DefaultResultMapper;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.mapping.context.MappingContext;

public class PresidioResultMapper extends DefaultResultMapper {

    private MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext;

    private AssociationsResolver associationsResolver;

    public PresidioResultMapper(MappingContext<? extends ElasticsearchPersistentEntity<?>, ElasticsearchPersistentProperty> mappingContext) {
        super(mappingContext);
        this.mappingContext = mappingContext;
    }

    @Override
    public <T> T mapEntity(String source, Class<T> clazz) {
        T result = super.mapEntity(source, clazz);
        result = mapAssociations(result,clazz);
        return result;
    }


    public <T> T mapAssociations(T result, Class<T> clazz) {

        if (mappingContext != null && clazz.isAnnotationPresent(Document.class)) {

            ElasticsearchPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(clazz);

            persistentEntity.doWithAssociations(new SimpleAssociationHandler() {

                public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {

                    ElasticsearchAssociation esAssociation = (ElasticsearchAssociation) association;
                    Object obj = associationsResolver.resolveAssociation(result, esAssociation);
                    persistentEntity.getPropertyAccessor(result).setProperty(association.getInverse(), obj);

                }
            });
        }

        return result;
    }

    public void setResolver(AssociationsResolver associationsResolver) {
        this.associationsResolver = associationsResolver;
    }

}