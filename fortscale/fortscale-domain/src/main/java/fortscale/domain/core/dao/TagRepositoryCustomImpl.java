package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by Amir Keren on 02/12/15.
 */
public class TagRepositoryCustomImpl implements TagRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void addTag(Tag tag) {
		mongoTemplate.insert(tag);
	}

	@Override
	public void removeTag(Tag tag) {
		mongoTemplate.remove(tag);
	}

}