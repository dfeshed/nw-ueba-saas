package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by Amir Keren on 02/12/15.
 */
public class TagRepositoryImpl implements TagRepositoryCustom {

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
		Query query = new Query();
		query.addCriteria(Criteria.where(Tag.nameField).is(tag.getName()));
		Update update = new Update();
		update.set(Tag.nameField, tag.getName());
		update.set(Tag.displayNameField, tag.getDisplayName());
		update.set(Tag.createsIndicatorField, tag.getCreatesIndicator());
		update.set(Tag.rulesField, tag.getRules());
		update.set(Tag.deletedField, tag.getDeleted());
		update.set(Tag.isAssignableField, tag.getIsAssignable());
		mongoTemplate.upsert(query, update, Tag.class);
	}

	@Override
	public void addTag(Tag tag) {
		mongoTemplate.insert(tag);
	}

	@Override
	public void removeTag(Tag tag) {
		mongoTemplate.remove(tag);
	}

	@Override
	public List<Tag> findAll(boolean includeDeleted) {
		if (includeDeleted) {
			return mongoTemplate.findAll(Tag.class);
		} else {
			Query query = query(where(Tag.deletedField).ne(false));
			return mongoTemplate.find(query,Tag.class);
		}

	}

}