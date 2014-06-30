package fortscale.domain.core.dao;


import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.Computer;

public interface ComputerRepository extends MongoRepository<Computer, String>, ComputerRepositoryCustom {

	Computer findByName(String name);
}
