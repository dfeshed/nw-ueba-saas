package fortscale.domain.core.dao;
        
        import fortscale.domain.core.EntityType;
        import fortscale.domain.core.Evidence;
        import fortscale.domain.core.EvidenceType;
        import org.springframework.data.mongodb.repository.MongoRepository;
        
        import java.util.List;
        
        /**
  */
        public interface GeoHoppingRepository extends MongoRepository<Evidence,String>, GeoHoppingRepositoryCustom {
    
            
            }