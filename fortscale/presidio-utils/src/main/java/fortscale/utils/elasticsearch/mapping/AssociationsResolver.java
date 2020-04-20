package fortscale.utils.elasticsearch.mapping;


import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;

/**
 * Used to resolve associations annotated with {@link fortscale.utils.elasticsearch.annotations.EsAssociation}.
 */
public interface AssociationsResolver {


    /**
     *  Resolves the given {@link ElasticsearchAssociation} into an object of the given association observed {@link ElasticsearchPersistentProperty}'s type.
     *
     *  The method might return a proxy object for the {@link ElasticsearchAssociation} or resolve it immediately.
     *
     * @param inversedObject
     * @param association association the {@link ElasticsearchAssociation} to resolve.
     * @return
     */
    Object resolveAssociation(Object inversedObject , ElasticsearchAssociation association);


    /**
     * Actually loads the {@link ElasticsearchAssociation} from the datasource.
     *
     * @param association must not be {@literal null}.
     * @return
     * @since 1.7
     */
    <T> T fetchAssociation(Object inversedObject , ElasticsearchAssociation association);

}
