package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by Amir Keren on 02/12/15.
 */
public class TagRepositoryCustomImpl implements TagRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void updateCreatesIndicator(String name, boolean createsIndicator) {
		Query query = new Query();
		query.addCriteria(Criteria.where(Tag.nameField).is(name));
		Tag tag = mongoTemplate.findOne(query, Tag.class);
		tag.setCreatesIndicator(createsIndicator);
		mongoTemplate.save(tag);
	}

	@Override
	public void updateTag(Tag tag) {
		mongoTemplate.save(tag);
	}

	@Override
	public void addTag(Tag tag) {
		mongoTemplate.insert(tag);
	}

	@Override
	public void addTags(List<Tag> tags) {
		mongoTemplate.insert(tags);
	}

	@Override
	public void removeTag(Tag tag) {
		mongoTemplate.remove(tag);
	}

	@Override
	public List<Tag> findAll() {
		return mongoTemplate.findAll(Tag.class);
	}

}