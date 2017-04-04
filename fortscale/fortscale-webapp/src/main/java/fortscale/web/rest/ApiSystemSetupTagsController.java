package fortscale.web.rest;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.core.Tag;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.TagService;
import fortscale.services.UserTagService;
import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.services.users.tagging.UserTaggingTaskPersistencyServiceImpl;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.ResponseEntityMessage;
import fortscale.web.beans.request.UserTaggingFilePath;
import fortscale.web.rest.Utils.TaskAction;
import fortscale.web.services.TaskService;
import fortscale.web.services.UserTaggingTaskServiceImpl;
import fortscale.web.tasks.ControllerInvokedUserTaggingTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @RequestMapping(value = "/save_tagging_path", method = RequestMethod.PUT)
    @ApiOperation(value = "Save the tagging file path and mode")
    public ResponseEntity<ResponseEntityMessage> savePath(@RequestBody UserTaggingFilePath userTaggingFilePath) {
        userTaggingTaskPersistenceService.saveSystemSetupTaggingFilePath(userTaggingFilePath.getUserTaggingFilePath());
        return new ResponseEntity<>(new ResponseEntityMessage("tagging path saved"), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete_tagging_path", method = RequestMethod.DELETE)
    @ApiOperation(value = "Save the tagging file path and mode")
    public ResponseEntity<ResponseEntityMessage> deletePath() {
        userTaggingTaskPersistenceService.deleteSystemSetupTaggingFilePath();
        return new ResponseEntity<>(new ResponseEntityMessage("tagging path deleted"), HttpStatus.OK);
    }

    @RequestMapping(value = "/get_tagging_path", method = RequestMethod.GET)
    @ApiOperation(value = "Get the tagging file path and mode", response = UserTaggingFilePath.class)
    public UserTaggingFilePath getPath() {
        return new UserTaggingFilePath(userTaggingTaskPersistenceService.getSystemSetupUserTaggingFilePath());
    }

    @RequestMapping(value = "/run_tagging_task", method = RequestMethod.POST)
    @ApiOperation(value = "Run user tagging job in different process and reports the result to web socket")
    public ResponseEntity<ResponseEntityMessage> runUserTagging(@RequestBody UserTaggingFilePath userTaggingFilePath) {
        try {
            logger.debug("Executing user tagging");
            logger.debug("Adding tagging file path {}", userTaggingFilePath.getUserTaggingFilePath());
            ((UserTaggingTaskServiceImpl) userTaggingTaskService).setUserTaggingFilePath(userTaggingFilePath.getUserTaggingFilePath());

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
    @ApiOperation(value = "Stop user tagging job which runs in a different process")
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

    @RequestMapping(method = RequestMethod.GET, value = "/tagging_task_status")
    @LogException
    @ApiOperation(value = "Get user tagging job status")
    public UserTaggingExecutionStatus getJobStatus() {
        Map<String, Long> usersAffected = new HashMap<>();
        Long lastExecutionFinishTime = null;
        Long lastExecutionStartTime = null;
        boolean isRunning = false;
        String errorMessage = null;
        if (isRunning()) {
            lastExecutionStartTime = lastUserTaggingExecutionStartTime;
            isRunning = true;
        } else {
            UserTaggingTaskPersistencyServiceImpl.UserTaggingResult taskResults = userTaggingTaskPersistenceService.getTaskResults(UserTaggingTaskPersistenceService.USER_TAGGING_RESULT_ID);
            lastExecutionFinishTime = userTaggingTaskPersistenceService.getLastExecutionTime();
            isRunning = false;
            if (taskResults!= null) {
                usersAffected = taskResults.getUsersAffected();
                errorMessage =taskResults.getErrorMessage();
            }
        }

        return new UserTaggingExecutionStatus(lastExecutionFinishTime, lastExecutionStartTime, isRunning, usersAffected, errorMessage);
    }

    /**
     * this method returns the running mode (running not running)
     * @return true - running false - not running
     */
    private Boolean isRunning() {
        Set<ControllerInvokedUserTaggingTask> activeTasks = userTaggingTaskService.getActiveTasks();

        return activeTasks.size() > 0;
    }

    @ApiModel()
    public static class UserTaggingExecutionStatus {

        @ApiModelProperty(value = "Last execution finish time")
        private final Long lastExecutionFinishTime;
        @ApiModelProperty(value = "Last execution start time")
        private final Long lastExecutionStartTime;
        @ApiModelProperty(value = "Is the tagging process currently running")
        private final boolean isRunning;
        @ApiModelProperty(value = "Number of users affected by the tagging process per tag")
        private final Map<String, Long> usersAffected;
        @ApiModelProperty(value = "Error message we received during running the tagging")
        private final String errorMessage;


        public UserTaggingExecutionStatus(Long lastExecutionFinishTime, Long lastExecutionStartTime, boolean isRunning, Map<String, Long> usersAffected, String errorMessage) {
            this.lastExecutionFinishTime = lastExecutionFinishTime;
            this.lastExecutionStartTime = lastExecutionStartTime;
            this.isRunning = isRunning;
            this.usersAffected = usersAffected;
            this.errorMessage = errorMessage;
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

        public Map<String, Long> getUsersAffected() {
            return usersAffected;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}


