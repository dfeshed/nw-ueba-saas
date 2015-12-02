package fortscale.domain.core.dao;

import fortscale.domain.core.Tag;

/**
 * Created by Amir Keren on 02/12/15.
 */
public interface TagRepositoryCustom {

	void addTag(Tag tag);
	void removeTag(Tag tag);

}