package fortscale.web.rest;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdObject;
import fortscale.domain.core.Tag;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.TagService;
import fortscale.services.UserTagService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.spring.SpringPropertiesUtil;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.ResponseEntityMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/api/tags")
public class ApiSystemSetupTagsController extends BaseController {

    private static final Logger logger = Logger.getLogger(ApiSystemSetupTagsController.class);
    public static final String CHARS_TO_REMOVE_FROM_TAG_RULE = "\n";

    private String COLLECTION_TARGET_DIR;
    private String USER_HOME_DIR;

    private static final String SUCCESSFUL_RESPONSE = "Successful";
    private static final String KEY_GROUPS = "groups";
    private static final String KEY_OUS = "ous";


    private final TagService tagService;
    private final UserTagService userTagService;
    private final ActiveDirectoryService activeDirectoryService;
    private AtomicBoolean taggingTaskInProgress = new AtomicBoolean(false);


    @Autowired
    public ApiSystemSetupTagsController(TagService tagService, UserTagService userTagService, ActiveDirectoryService activeDirectoryService) {
        this.tagService = tagService;
        this.userTagService = userTagService;
        this.activeDirectoryService = activeDirectoryService;
    }

    @PostConstruct
    private void getProperties() {
        final String homeDirProperty = SpringPropertiesUtil.getProperty("user.home.dir");
        USER_HOME_DIR = homeDirProperty != null ? homeDirProperty : "/home/cloudera";

        COLLECTION_TARGET_DIR =  USER_HOME_DIR + "/fortscale/fortscale-core/fortscale/fortscale-collection/target";

        final String userName = SpringPropertiesUtil.getProperty("user.name");
    }


    /**
     * This method gets all the tags in the tags collection
     */
    @RequestMapping(value="/user_tags", method= RequestMethod.GET)
    @ResponseBody
    @LogException
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
    public ResponseEntity<ResponseEntityMessage> updateTags(@RequestBody @Valid List<Tag> tags) {
        logger.info("Updating {} tags", tags.size());
        for (Tag tag: tags) {
            tag.setRules(sanitizeRules(tag.getRules()));
            if (!tagService.updateTag(tag)) {
                return new ResponseEntity<>(new ResponseEntityMessage("failed to update tag"), HttpStatus.INTERNAL_SERVER_ERROR);
                //if update was successful and tag is no longer active - remove that tag from all users
            } else if (tag.getDeleted()) {
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

    /**
     * This method executes the user tagging task
     * @return the HTTP status of the request and an error message if there was an error
     */
    @RequestMapping(value="/run_tagging_task", method=RequestMethod.GET)
    @LogException
    public ResponseEntity<ResponseEntityMessage> runTaggingTask() {
        if (taggingTaskInProgress.compareAndSet(false, true)) {
            logger.info("Starting Tagging task from deployment wizard");
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(this::executeRunningTask);

            executorService.shutdown();
            return new ResponseEntity<>(new ResponseEntityMessage(SUCCESSFUL_RESPONSE), HttpStatus.OK);

        }
        else {
            final String msg = "Tagging task is already in progress. Can't execute again until the previous execution is finished. Request to execute ignored.";
            logger.warn(msg);
            return new ResponseEntity<>(new ResponseEntityMessage(msg), HttpStatus.LOCKED);
        }
    }

    private void executeRunningTask() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.warn("Tagging task from deployment wizard has been terminated using a shutdown hook (probably kill signal or ^C)");
            taggingTaskInProgress.set(false);
        }));

        try {
            Process process;
            try {
                String jarPath = COLLECTION_TARGET_DIR + "/fortscale-collection-1.1.0-SNAPSHOT.jar";
                final ArrayList<String> arguments = new ArrayList<>(Arrays.asList("java", "-jar", jarPath, "User", "Tagging"));
                final ProcessBuilder processBuilder = new ProcessBuilder(arguments);
                processBuilder.directory(new File(COLLECTION_TARGET_DIR));
                processBuilder.redirectErrorStream(true);
                process = processBuilder.start();
            } catch (IOException e) {
                final String msg = "Execution of tagging task from deployment wizard has failed. " + e.getLocalizedMessage();
                logger.error(msg);
                return;
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
                return;
            }
            if (status != 0) {
                try {
                    String processOutput = IOUtils.toString(process.getInputStream());
                    final int length = processOutput.length();
                    if (length > 1000) {
                        processOutput = processOutput.substring(length - 1000, length); // getting last 1000 chars to not overload the log file
                    }
                    logger.error("Error stream for job User Tagging = \n{}", processOutput);
                } catch (IOException e) {
                    logger.warn("Failed to get error stream from process for job User Tagging");
                }
                final String msg = String.format("Execution of task User Tagging has finished with status %s. Execution failed", status);
                logger.error(msg);
                return;
            }
            else {
                logger.info("Tagging task from deployment wizard has finished successfully");
                return;
            }
        } finally {
            taggingTaskInProgress.set(false);
        }
    }

}


