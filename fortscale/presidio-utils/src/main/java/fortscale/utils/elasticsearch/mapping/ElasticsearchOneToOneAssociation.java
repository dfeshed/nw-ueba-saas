package fortscale.utils.elasticsearch.mapping;

import org.springframework.data.elasticsearch.core.mapping.ElasticsearchPersistentProperty;

public class ElasticsearchOneToOneAssociation extends ElasticsearchAssociation {

    public ElasticsearchOneToOneAssociation(ElasticsearchPersistentProperty inverse, ElasticsearchPersistentProperty obverse) {
        super(inverse, obverse);
    }


}
