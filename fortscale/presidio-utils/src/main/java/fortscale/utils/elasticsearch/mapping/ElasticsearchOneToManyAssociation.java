package fortscale.utils.elasticsearch.mapping;

import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;

public class ElasticsearchOneToManyAssociation extends ElasticsearchAssociation {

    public ElasticsearchOneToManyAssociation(ElasticsearchPersistentProperty inverse, ElasticsearchPersistentProperty obverse) {
        super(inverse, obverse);
    }



}
