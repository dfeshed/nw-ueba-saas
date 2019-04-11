package fortscale.utils.elasticsearch.mapping;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.GetQuery;

/**
 * A {@link AssociationsResolver} that resolves {@link ElasticsearchAssociation}s by generating lazy loading proxies.
 */
public class DefaultAssociationsResolver implements AssociationsResolver {

    ElasticsearchOperations elasticsearchOperations;

    public DefaultAssociationsResolver(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public Object resolveAssociation(Object inversedObject, ElasticsearchAssociation association) {
        return Enhancer.create(association.getInverse().getType(),
                new LazyLoader() {
                    public Object loadObject() throws Exception {
                        return fetchAssociation(inversedObject, association);
                    }
                });
    }

    @Override
    public <T> T fetchAssociation(Object inversedObject, ElasticsearchAssociation association) {

        if (association instanceof ElasticsearchOneToOneAssociation) {

            if (association.getObverse().isIdProperty()) {

                GetQuery getQuery = new GetQuery();
                Object id = association.getInversePersistentEntity().getPropertyAccessor(inversedObject).getProperty(association.getInverseJoinProperty());
                getQuery.setId(id.toString());
                T obj = (T) elasticsearchOperations.queryForObject(getQuery, association.getObversePersistentEntity().getType());

                return obj;
            }
        }

        if (association instanceof ElasticsearchOneToManyAssociation) {

            String fieldName = association.getObverse().getFieldName();
            Object value = association.getInversePersistentEntity().getPropertyAccessor(inversedObject).getProperty(association.getInverseJoinProperty());
            PageRequest page = new PageRequest(0, 1000);
            CriteriaQuery query = new CriteriaQuery(new Criteria(fieldName).is(value), page);

            T obj = (T) elasticsearchOperations.queryForList(query, association.getObversePersistentEntity().getType());

            return obj;
        }

        return null;
    }



}
