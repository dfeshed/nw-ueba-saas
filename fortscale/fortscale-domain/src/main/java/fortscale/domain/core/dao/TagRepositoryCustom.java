package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;

import java.util.List;

/**
 * Created by Amir Keren on 02/12/15.
 */
public interface TagRepositoryCustom {

	void updateCreatesIndicator(String name, boolean createsIndicator);
	void updateTags(List<Tag> tags);
	void addTag(Tag tag);
	void removeTag(Tag tag);
	List<Tag> findAll();

}