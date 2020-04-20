package fortscale.utils.elasticsearch.mapping;

import fortscale.utils.elasticsearch.PresidioElasticsearchPersistentEntity;
import fortscale.utils.elasticsearch.annotations.JoinColumn;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentEntity;
import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.mapping.Association;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.util.StringUtils;

public class ElasticsearchAssociation extends Association<ElasticsearchPersistentProperty> {


    private JoinColumn joinColumn;

    /**
     * Creates a new {@link Association} between the two given {@link PersistentProperty}s.
     *
     * @param inverse
     * @param obverse
     */
    public ElasticsearchAssociation(ElasticsearchPersistentProperty inverse, ElasticsearchPersistentProperty obverse) {
        super(inverse, obverse);
        if (null != inverse) {
            joinColumn = inverse.findAnnotation(JoinColumn.class);
        }
    }

    public ElasticsearchPersistentEntity<?> getObversePersistentEntity() {

        if (null != getInverse()) {
            PresidioElasticsearchPersistentEntity owner = (PresidioElasticsearchPersistentEntity) getInverse().getOwner();
            if (null == owner) {
                return null;
            }
            SimpleElasticsearchMappingContext context = owner.getContext();
            if (null == context) {
                return null;
            }

            return context.getPersistentEntity(getInverse().getActualType());

        }
        return null;
    }


    public ElasticsearchPersistentEntity<?> getInversePersistentEntity() {

        if (null != getInverse()) {
            ElasticsearchPersistentEntity owner = (ElasticsearchPersistentEntity) getInverse().getOwner();
            return owner;

        }
        return null;
    }

    @Override
    public ElasticsearchPersistentProperty getObverse() {

        ElasticsearchPersistentEntity<?> entity = getObversePersistentEntity();
        if (null == entity) {
            return null;
        }
        if (null == joinColumn || StringUtils.isEmpty(joinColumn.referencedColumnName())) {
            return entity.getIdProperty();
        }

        return entity.getPersistentProperty(joinColumn.referencedColumnName());

    }


    public ElasticsearchPersistentProperty getInverseJoinProperty() {

        ElasticsearchPersistentEntity<?> entity = getInversePersistentEntity();
        if (null == entity) {
            return null;
        }
        if (null == joinColumn || StringUtils.isEmpty(joinColumn.name())) {
            return entity.getIdProperty();
        }

        return entity.getPersistentProperty(joinColumn.name());

    }


    public String getJoinColumnName() {
        String name = null;
        if (null != joinColumn && StringUtils.hasText(joinColumn.name())) {
            return joinColumn.name();
        } else {
            ElasticsearchPersistentEntity<?> entity = getObversePersistentEntity();
            if (null != entity && entity.hasIdProperty()) {
                name = entity.getIdProperty().getFieldName();
            }
        }
        return name;
    }

    public String getJoinReferencedColumnName() {
        if (null != joinColumn && StringUtils.hasText(joinColumn.referencedColumnName())) {
            return joinColumn.referencedColumnName();
        }
        ElasticsearchPersistentProperty obverse = getObverse();
        if (null != obverse) {
            return obverse.getFieldName();
        }

        return null;
    }
}
