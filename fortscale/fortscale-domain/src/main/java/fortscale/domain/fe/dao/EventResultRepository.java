package fortscale.domain.fe.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.fe.EventResult;



public interface EventResultRepository extends MongoRepository<EventResult, String>, EventResultRepositoryCustom{

}
