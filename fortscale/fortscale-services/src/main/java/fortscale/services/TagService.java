package fortscale.services;

import fortscale.domain.core.Tag;

import java.util.Collection;
import java.util.List;

public interface TagService {

	List<Tag> getAllTags();
	boolean addTag(Tag tag);
	boolean removeTag(Tag tag);
	Tag getTag(String name);
	boolean updateTags(List<Tag> tags);

}