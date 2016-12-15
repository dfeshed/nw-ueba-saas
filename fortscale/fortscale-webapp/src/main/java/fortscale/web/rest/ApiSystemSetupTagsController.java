package fortscale.web.rest;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.core.Tag;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.TagService;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/api/tags")
public class ApiSystemSetupTagsController extends BaseController {

    private static final Logger logger = Logger.getLogger(ApiSystemSetupTagsController.class);
    private static final String EMPTY_RESPONSE_STRING = "{}";
    private static final String KEY_GROUPS = "groups";
    private static final String KEY_OUS = "ous";
    public static final String COLLECTION_JAR_NAME = "${user.home.dir}/fortscale/fortscale-core/fortscale/fortscale-collection/target/fortscale-collection-1.1.0-SNAPSHOT.jar";

    private final TagService tagService;
    private final UserTagService userTagService;
    private final UserService userService;
    private final ActiveDirectoryService activeDirectoryService;
    private AtomicBoolean taggingTaskInProgress = new AtomicBoolean(false);


    @Autowired
    public ApiSystemSetupTagsController(TagService tagService, UserTagService userTagService, UserService userService, ActiveDirectoryService activeDirectoryService) {
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.userService = userService;
        this.activeDirectoryService = activeDirectoryService;
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
     * @return the HTTP status of the request and an error message if there was an error
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
                userTagService.removeTagFromAllUsers(tagName);
            }
        }
        return new ResponseEntity<>(EMPTY_RESPONSE_STRING, HttpStatus.ACCEPTED);
    }


    /**
     * This method adds/removes tags to/from the users in the users collection
     * @return the HTTP status of the request and an error message if there was an error
     */
    @RequestMapping(value="/tagUsers", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<String> tagUsers() {
        try {
            userTagService.update();
        } catch (Exception ex) {
            return new ResponseEntity<>("{" + ex.getLocalizedMessage() + "}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(EMPTY_RESPONSE_STRING, HttpStatus.OK);
    }


    @RequestMapping(value="/search", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<Map<String, List<? extends AdObject>>> searchGroupsAndOusByNameStartingWith(String startsWith) {
        try {
            logger.debug("Searching for groups and OUs stating with {}", startsWith);
            final List<AdGroup> groups = activeDirectoryService.getGroupsByNameContains(startsWith);
            final List<AdOU> ous = activeDirectoryService.getOusByOuContains(startsWith);
            final HashMap<String, List<? extends AdObject>> resultsMap = new HashMap<>();
            resultsMap.put(KEY_GROUPS, groups);
            resultsMap.put(KEY_OUS, ous);
            return new ResponseEntity<>(resultsMap, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to search for groups and OUs", ex);
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method executes the user tagging task
     * @return the HTTP status of the request and an error message if there was an error
     */
    @RequestMapping(value="/run_tagging_task", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<String> runTaggingTask() {
        if (taggingTaskInProgress.compareAndSet(false, true)) {
            logger.debug("Starting Tagging task from deployment wizard");
            Process process;
            try {
                final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", COLLECTION_JAR_NAME, "User", "Tagging"));
                process = new ProcessBuilder(arguments).start();
            } catch (IOException e) {
                final String msg = "Execution of tagging task from deployment wizard has failed. " + e.getLocalizedMessage();
                logger.error(msg);
                taggingTaskInProgress.set(false);
                return new ResponseEntity<>("{" + msg + "}", HttpStatus.BAD_REQUEST);
            }
            int status;
            try {
                status = process.waitFor();
            } catch (InterruptedException e) {
                if (process.isAlive()) {
                    logger.error("Killing the process forcibly");
                    process.destroyForcibly();
                }
                final String msg = "Execution of tagging task from deployment wizard has been interrupted. Task failed. " + e.getLocalizedMessage();
                logger.error(msg);
                taggingTaskInProgress.set(false);
                return new ResponseEntity<>("{" + msg + "}", HttpStatus.BAD_REQUEST);
            }


            if (status != 0) {
                final String msg = String.format("Execution of tagging task from deployment wizard has finished with status %d. Task failed.", status);
                taggingTaskInProgress.set(false);
                return new ResponseEntity<>("{" + msg + "}", HttpStatus.BAD_REQUEST);
            }
            else {
                logger.debug("Tagging task from deployment wizard has finished successfully");
                taggingTaskInProgress.set(false);
                return new ResponseEntity<>(EMPTY_RESPONSE_STRING, HttpStatus.OK);
            }
        }
        else {
            final String msg = "Tagging task is already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.";
            logger.warn(msg);
            return new ResponseEntity<>("{" + msg + "}", HttpStatus.LOCKED);
        }
    }

}


