package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.domain.core.dao.TagRepository;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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


	@PostConstruct
	public void afterInit(){
		this.verifyDeletedTagsNotExistsOnAnyUser();
	}


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

	/**
	 * *
	 * Update tag cannot delete tag and cannot update deleted tag.
	 * To delete tag use deleteTag method.
	 * To update tag, first mark it as not deleted
	 *
	 * @param tag - the new tag value
	 * @return
	 */
	@Override
	public boolean updateTag(Tag tag) {
		if (tag.getName().length() > maxTagLength || tag.getDisplayName().length() > maxTagLength) {
			logger.error("failed to update tag {} - tag is too long! (needs to be under {} characters)", tag,
					maxTagLength);
			return false;
		}

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

	/**
	 *
	 * @param tagName the uniqie name of the tag
	 * @return true if deleted successfuly.
	 */
	public boolean deleteTag(String tagName){
		Tag tag = tagRepository.findByNameIgnoreCase(tagName);
		if (tag==null){
			logger.error("Try to delete not existing tag named {}", tagName);
			return false;
		}

		tag.setDeleted(true);
		tagRepository.updateTag(tag);
		userService.removeTagFromAllUsers(tagName);
		return  true;
	}

	private void verifyDeletedTagsNotExistsOnAnyUser(){
		for (Tag t : tagRepository.findAll()){
			if (BooleanUtils.isNotFalse(t.getDeleted())){ //If false => if not true and not null
				userService.removeTagFromAllUsers(t.getName());
			}
		}
	}


}