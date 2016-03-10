package fortscale.domain.core.dao;
        
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.GeoHopping;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
* Repository for GeoHopping events. Regardless to data source.
*/
public interface GeoHoppingRepository extends MongoRepository<GeoHopping,String>, GeoHoppingRepositoryCustom {


}