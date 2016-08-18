package fortscale.domain.core.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.Computer;

import java.util.List;

public interface ComputerRepository extends MongoRepository<Computer, String>, ComputerRepositoryCustom {

	Computer findByName(String name);
	List<Computer> findByNameIn(String... names);
}
