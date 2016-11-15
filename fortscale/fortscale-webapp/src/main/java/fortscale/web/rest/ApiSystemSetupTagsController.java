package fortscale.web.rest;

import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ApiSystemSetupTagsController extends BaseController {

    private static final Logger logger = Logger.getLogger(ApiSystemSetupTagsController.class);
    public static final String EMPTY_RESPONSE_STRING = "{}";

    private final TagService tagService;

    private final UserTagService userTagService;

    private UserService userService;


    @Autowired
    public ApiSystemSetupTagsController(TagService tagService, UserTagService userTagService, UserService userService) {
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.userService = userService;
    }


    /**
     * This method gets all the tags in the tags collection
     */
    @RequestMapping(value="/user_tags", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<Tag>> getAllTags() {
        logger.info("Getting all tags");
        List<Tag> result = tagService.getAllTags();
        DataBean<List<Tag>> response = new DataBean<>();
        response.setData(result);
        response.setTotal(result.size());
        return response;
    }


    /**
     * This method updates tags in the tags collection and removes newly-inactive tags from the users collection
     * @return
     */
    @RequestMapping(value="/user_tags", method=RequestMethod.POST)
    @LogException
    public ResponseEntity<String> updateTags(@RequestBody @Valid List<Tag> tags) {
        logger.info("Updating {} tags", tags.size());
        for (Tag tag: tags) {
            if (!tagService.updateTag(tag)) {
                return new ResponseEntity<>("{failed to update tag}", HttpStatus.INTERNAL_SERVER_ERROR);
                //if update was successful and tag is no longer active - remove that tag from all users
            } else if (!tag.getActive()) {
                String tagName = tag.getName();
                Set<String> usernames = userService.findUsernamesByTags(new String[] { tagName });
                if (CollectionUtils.isNotEmpty(usernames)) {
                    logger.info("tag {} became inactive, removing from {} users", tagName, usernames.size());
                    for (String username : usernames) {
                        userTagService.removeUserTags(username, Collections.singletonList(tagName));
                    }
                }
            }
        }
        return new ResponseEntity<>(EMPTY_RESPONSE_STRING, HttpStatus.ACCEPTED);
    }


    /**
     * This method adds/removes tags to/from the users in the users collection
     * @return
     */
    @RequestMapping(value="/tagUsers", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<String> tagUsers() {
        try {
            //TODO - make this asynchronous
            userTagService.update();
        } catch (Exception ex) {
            return new ResponseEntity<>("{" + ex.getLocalizedMessage() + "}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(EMPTY_RESPONSE_STRING, HttpStatus.OK);
    }
}
