package fortscale.web.rest;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.core.Tag;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.TagService;
import fortscale.services.UserTagService;
import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.ResponseEntityMessage;
import fortscale.web.rest.Utils.TaskAction;
import fortscale.web.services.TaskService;
import fortscale.web.tasks.ControllerInvokedUserTaggingTask;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/api/tags")
public class ApiSystemSetupTagsController extends BaseController {

    private static final Logger logger = Logger.getLogger(ApiSystemSetupTagsController.class);
    public static final String CHARS_TO_REMOVE_FROM_TAG_RULE = "\n";
    private static final long TIMEOUT_IN_SECONDS = 60;

    private static final String SUCCESSFUL_RESPONSE = "Successful";
    private static final String KEY_GROUPS = "groups";
    private static final String KEY_OUS = "ous";
    private static final String DESTINATION_TASK_RESPONSE = "/wizard/user_tagging_response";
    private static final String DESTINATION_ACTIONS = "/wizard/user_tagging_actions";

    private final TagService tagService;
    private final UserTagService userTagService;
    private final ActiveDirectoryService activeDirectoryService;
    private TaskService userTaggingTaskService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private Long lastUserTaggingExecutionStartTime;
    private UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;


    @Autowired
    public ApiSystemSetupTagsController(TagService tagService, UserTagService userTagService, ActiveDirectoryService activeDirectoryService,
                                        @Qualifier(value = "UserTaggingTaskServiceImpl") TaskService userTaggingTaskService, SimpMessagingTemplate simpMessagingTemplate,
                                        UserTaggingTaskPersistenceService userTaggingTaskPersistenceService) {
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.activeDirectoryService = activeDirectoryService;
        this.userTaggingTaskService = userTaggingTaskService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userTaggingTaskPersistenceService = userTaggingTaskPersistenceService;
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
    public ResponseEntity<ResponseEntityMessage> updateTags(@RequestBody @Valid List<Tag> tags) {
        logger.info("Updating {} tags", tags.size());
        for (Tag tag: tags) {
            tag.setRules(sanitizeRules(tag.getRules()));
            if (!tagService.updateTag(tag)) {
                return new ResponseEntity<>(new ResponseEntityMessage("failed to update tag"), HttpStatus.INTERNAL_SERVER_ERROR);
                //if update was successful and tag is no longer active - remove that tag from all users
            } else if (!tag.getActive()) {
                String tagName = tag.getName();
                userTagService.removeTagFromAllUsers(tagName);
            }
        }
        return new ResponseEntity<>(new ResponseEntityMessage(SUCCESSFUL_RESPONSE), HttpStatus.ACCEPTED);
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

    @RequestMapping(value="/search", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<Map<String, List<? extends AdObject>>> searchGroupsAndOusByNameContains(String containedText) {
        try {
            logger.info("Searching for AD Groups and OUs whose names contain {}", containedText);
            final List<AdGroup> groups = activeDirectoryService.getGroupsByNameContains(containedText);
            final List<AdOU> ous = activeDirectoryService.getOusByOuContains(containedText);
            final HashMap<String, List<? extends AdObject>> resultsMap = new HashMap<>();
            resultsMap.put(KEY_GROUPS, groups);
            resultsMap.put(KEY_OUS, ous);
            return new ResponseEntity<>(resultsMap, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Failed to search for groups and OUs", ex);
            return new ResponseEntity<>(Collections.emptyMap(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/run_tagging_task" )
    public ResponseEntity<ResponseEntityMessage> runUserTagging() {
        try {
            logger.debug("Executing user tagging");
            final boolean executedSuccessfully = userTaggingTaskService.executeTasks(simpMessagingTemplate, DESTINATION_TASK_RESPONSE);
            if (executedSuccessfully) {
                lastUserTaggingExecutionStartTime = System.currentTimeMillis();
                simpMessagingTemplate.convertAndSend(DESTINATION_ACTIONS, TaskAction.EXECUTE);
                return new ResponseEntity<>(new ResponseEntityMessage("User tagging is running."), HttpStatus.OK);
            }
            else {
                final String inProgressMsg = "User tagging already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.";
                logger.warn(inProgressMsg);
                return new ResponseEntity<>(new ResponseEntityMessage(inProgressMsg), HttpStatus.LOCKED);
            }
        } catch (Exception e) {
            final String msg = "Failed to stop user tagging execution";
            logger.error(msg, e);
            return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping("/stop_tagging_task" )
    public ResponseEntity<ResponseEntityMessage> cancelUserTaggingExecution() {
        try {
            logger.debug("Cancelling user tagging execution");
            if (userTaggingTaskService.cancelAllTasks(TIMEOUT_IN_SECONDS)) {
                lastUserTaggingExecutionStartTime = null;
                final String message = "User tagging execution has been cancelled successfully";
                logger.debug(message);
                simpMessagingTemplate.convertAndSend(DESTINATION_ACTIONS, TaskAction.CANCEL);
                return new ResponseEntity<>(new ResponseEntityMessage(message), HttpStatus.OK);
            }
            else {
                final String msg = "Failed to cancel user tagging execution";
                logger.error(msg);
                return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            final String msg = "Failed to cancel user tagging execution";
            logger.error(msg, e);
            return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET,value = "/tagging_task_status")
    @LogException
    public UserTaggingExecutionStatus getJobStatus() {
        if (isRunning()){
            return new UserTaggingExecutionStatus(-1l, lastUserTaggingExecutionStartTime, true);
        }else {

         return new UserTaggingExecutionStatus(userTaggingTaskPersistenceService.getLastExecutionTime(), -1l, false);
        }
    }

    /**
     * this method returns the running mode (running not running)
     * @return true - running false - not running
     */
    private Boolean isRunning() {
        Set<ControllerInvokedUserTaggingTask> activeTasks = userTaggingTaskService.getActiveTasks();

        return activeTasks.size() > 0;
    }

    public static class UserTaggingExecutionStatus {

        private final Long lastExecutionFinishTime;
        private final Long lastExecutionStartTime;
        private final boolean isRunning;


        public UserTaggingExecutionStatus(Long lastExecutionFinishTime, Long lastExecutionStartTime, boolean isRunning) {
            this.lastExecutionFinishTime = lastExecutionFinishTime;
            this.lastExecutionStartTime = lastExecutionStartTime;
            this.isRunning = isRunning;
        }

        public Long getLastExecutionFinishTime() {
            return lastExecutionFinishTime;
        }

        public Long getLastExecutionStartTime() {
            return lastExecutionStartTime;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }
}


