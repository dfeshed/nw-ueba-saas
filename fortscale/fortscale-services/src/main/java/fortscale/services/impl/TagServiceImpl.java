package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.dao.TagRepository;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("tagService")
public class TagServiceImpl implements TagService {

	private static Logger logger = Logger.getLogger(TagServiceImpl.class);

	@Value("${user.list.custom_tags.max.length}")
	private int maxTagLength;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private UserService userService;

	@Override
	public List<Tag> getAllTags(boolean includeDeleted) {
		return tagRepository.findAll(includeDeleted);
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
		return tagRepository.findByNameIgnoreCase(name);
	}

	@Override
	public boolean updateTag(Tag tag) {
		if (tag.getName().length() > maxTagLength || tag.getDisplayName().length() > maxTagLength) {
			logger.error("failed to update tag {} - tag is too long! (needs to be under {} characters)", tag,
					maxTagLength);
			return false;
		}
		//Update tag cannot delete tag and cannot update deleted tag.
		//To delete tag use deleteTag method.
		//To update tag, first mark it as not deleted
		if (tag.getDeleted()){
			logger.error("failed to update tag {} - deleted tag could not be update. To mark tag as deleted use tagService.deleteTag)", tag);
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

	public void deleteTag(String tagName){
		Tag tag = tagRepository.findByNameIgnoreCase(tagName);
		if (tag==null){
			logger.error("Try to delete not existing tag named {}", tagName);
			throw new RuntimeException("Can't delete tag "+ tagName +" because its not exists");
		}

		tag.setDeleted(true);
		tagRepository.updateTag(tag);
		userService.removeTagFromAllUsers(tagName);
	}

}