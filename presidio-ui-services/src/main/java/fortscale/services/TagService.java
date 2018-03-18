package fortscale.services;

import fortscale.domain.core.Tag;

import java.util.Collection;
import java.util.List;

public interface TagService {

	List<Tag> getAllTags(boolean includeDeleted);
	boolean addTag(Tag tag);
	Tag getTag(String name);
	boolean updateTag(Tag tag);
	boolean deleteTag(String name);

}