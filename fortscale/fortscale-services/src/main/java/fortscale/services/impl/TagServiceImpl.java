package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.dao.TagRepository;
import fortscale.services.TagService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("tagService")
public class TagServiceImpl implements TagService {

	private static Logger logger = Logger.getLogger(TagServiceImpl.class);

	@Value("${user.list.custom_tags.max.length}")
	private int maxTagLength;

	@Autowired
	private TagRepository tagRepository;

	@Override
	public List<Tag> getAllTags() {
		return tagRepository.findAll();
	}

	@Override
	public boolean addTag(Tag tag) {
		if (tag.getName().length() > maxTagLength || tag.getDisplayName().length() > maxTagLength) {
			logger.error("failed to add tag {} - tag is too long! (needs to be under {} characters)", tag,
					maxTagLength);
			return false;
		}
		try {
			tagRepository.addTag(tag);
		} catch (Exception ex) {
			logger.debug("failed to add tag {} - {}", tag, ex);
			return false;
		}
		return true;
	}

	@Override
	public Tag getTag(String name) {
		return tagRepository.findByName(name);
	}

	@Override
	public boolean updateTag(Tag tag) {
		if (tag.getName().length() > maxTagLength || tag.getDisplayName().length() > maxTagLength) {
			logger.error("failed to update tag {} - tag is too long! (needs to be under {} characters)", tag,
					maxTagLength);
			return false;
		}
		try {
			tagRepository.updateTag(tag);
		} catch (Exception ex) {
			logger.error("failed to update tag {} - {}", tag, ex);
			return false;
		}
		return true;
	}

}