package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.dao.TagRepository;
import fortscale.services.TagService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("tagService")
public class TagServiceImpl implements TagService {

	private static Logger logger = Logger.getLogger(TagServiceImpl.class);

	@Autowired
	private TagRepository tagRepository;

	@Override
	public List<Tag> getAllTags() {
		return tagRepository.findAll();
	}

	@Override
	public void addTag(Tag tag) {
		try {
			tagRepository.addTag(tag);
		} catch (Exception ex) {
			logger.debug("failed to add tag {}", tag);
		}
	}

	@Override
	public void removeTag(Tag tag) {
		tagRepository.removeTag(tag);
	}

}