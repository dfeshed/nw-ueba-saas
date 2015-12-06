package fortscale.services;

import fortscale.domain.core.Tag;

import java.util.Collection;
import java.util.List;

public interface TagService {

	List<Tag> getAllTags();
	void addTag(Tag tag);
	void removeTag(Tag tag);

}