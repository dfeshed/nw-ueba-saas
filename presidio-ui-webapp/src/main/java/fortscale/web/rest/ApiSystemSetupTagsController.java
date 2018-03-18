package fortscale.web.rest;


import fortscale.domain.core.Tag;

import fortscale.services.TagService;
import fortscale.services.UserTagService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.ResponseEntityMessage;

import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/api/tags")
@Api(value="/api/tags", description="This resource manage the tags",produces = "JSON", protocols = "HTTP,HTTPS")
public class ApiSystemSetupTagsController extends BaseController {

    private static final Logger logger = Logger.getLogger(ApiSystemSetupTagsController.class);
    public static final String CHARS_TO_REMOVE_FROM_TAG_RULE = "\n";

    private static final String SUCCESSFUL_RESPONSE = "Successful";

    private final TagService tagService;
    private final UserTagService userTagService;


    private SimpMessagingTemplate simpMessagingTemplate;
    private Long lastUserTaggingExecutionStartTime;



    @Autowired
    public ApiSystemSetupTagsController(TagService tagService, UserTagService userTagService) {
        this.tagService = tagService;
        this.userTagService = userTagService;

        this.simpMessagingTemplate = simpMessagingTemplate;

    }

    /**
     * This method gets all the tags in the tags collection
     */
    @RequestMapping(value="/user_tags", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    @ApiOperation(value = "Get all the tags exists in the system", response = DataBean.class)
    public DataBean<List<Tag>> getAllTags(@RequestParam(defaultValue = "false") boolean includeDeleted) {
        logger.info("Getting all tags");
        List<Tag> result = tagService.getAllTags(includeDeleted);
        DataBean<List<Tag>> response = new DataBean<>();
        response.setData(result);
        response.setTotal(result.size());
        return response;
    }


    /**
     * This method updates tags in the tags collection and removes newly-inactive tags from the users collection
     * @return the HTTP status of the request and an error message if there was an error
     */
    @RequestMapping(value="/user_tags", method=RequestMethod.POST)
    @LogException
    @ApiOperation(value = "Update tags")
    public ResponseEntity<ResponseEntityMessage> updateTags(@RequestBody @Valid List<Tag> tags) {
        logger.info("Updating {} tags", tags.size());
        for (Tag tag: tags) {
            tag.setRules(sanitizeRules(tag.getRules()));
            if (!tagService.updateTag(tag)) {
                return new ResponseEntity<>(new ResponseEntityMessage("failed to update tag "+tag.getDisplayName()), HttpStatus.INTERNAL_SERVER_ERROR);
                //if update was successful and tag is no longer active - remove that tag from all users
            } else if (tag.getDeleted()) {
                return new ResponseEntity<>(new ResponseEntityMessage("Can't delete deleted tag "+tag.getDisplayName()), HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(new ResponseEntityMessage(SUCCESSFUL_RESPONSE), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/{name}", method=RequestMethod.DELETE)
    @LogException
    @ApiOperation(value = "Delete tag")
    public ResponseEntity<ResponseEntityMessage> deleteTag(@PathVariable String name) {
        if (StringUtils.isBlank(name)){
            return  new ResponseEntity<ResponseEntityMessage>(new ResponseEntityMessage("Tag '"+name+"' not found"),HttpStatus.NOT_FOUND);
        }

        if (tagService.getTag(name) == null){
            return  new ResponseEntity<ResponseEntityMessage>(new ResponseEntityMessage("Tag '"+name+"' not found"),HttpStatus.NOT_FOUND);
        }

        boolean deletedSuccessfully = tagService.deleteTag(name);
        if (deletedSuccessfully){
            return  new ResponseEntity<ResponseEntityMessage>(HttpStatus.OK);
        } else {
            return  new ResponseEntity<ResponseEntityMessage>(new ResponseEntityMessage(" Cannot delete tag '"+name+"', check log for more information"),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private List<String> sanitizeRules(List<String> rules){
        List<String> sanitizedRules = new ArrayList<>();
        for (String rule: rules){

            String sanitized = rule.replaceAll(CHARS_TO_REMOVE_FROM_TAG_RULE,"");
            if (StringUtils.isNotBlank(sanitized)){
                sanitizedRules.add(sanitized);
            }

        }
        return sanitizedRules;
    }

    /**
     * This method adds/removes tags to/from the users in the users collection
     * @return the HTTP status of the request and an error message if there was an error
     */
    @RequestMapping(value="/tagUsers", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<ResponseEntityMessage> tagUsers() {
        try {
            logger.info("Updating all user-tags");
            userTagService.update();
        } catch (Exception ex) {
            return new ResponseEntity<>(new ResponseEntityMessage(ex.getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(new ResponseEntityMessage(SUCCESSFUL_RESPONSE), HttpStatus.OK);
    }

}


