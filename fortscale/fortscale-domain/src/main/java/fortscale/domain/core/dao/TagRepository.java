package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TagRepository extends MongoRepository<Tag, String>, TagRepositoryCustom {

	Tag findByNameIgnoreCase(String name);
	List<Tag> findByCreatesIndicator(Boolean createsIndicator);

}