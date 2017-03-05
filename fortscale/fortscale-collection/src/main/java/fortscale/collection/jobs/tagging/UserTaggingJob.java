package fortscale.collection.jobs.tagging;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.UserService;
import fortscale.services.UserTagService;
import fortscale.services.users.tagging.UserTaggingTaskPersistenceService;
import fortscale.services.users.tagging.UserTaggingTaskPersistencyServiceImpl;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@DisallowConcurrentExecution
public class UserTaggingJob extends FortscaleJob {

	private static final Logger logger = Logger.getLogger(UserTaggingJob.class);
	private static final String CSV_DELIMITER = ",";
	private static final int INDEX_USER_REGEX = 0;
	private static final int INDEX_TAGS = 1;
	private static final java.lang.String TAGS_DELIMITER = "|";

	private boolean useFile;

	@Value("${user.list.user_custom_tags.path:}")
	private String customTagFilePath;
	
	@Autowired
	private UserTagService userTagService;

	private String resultsId;

	@Autowired
	private UserTaggingTaskPersistenceService userTaggingTaskPersistenceService;
	@Autowired
	private UserService userService;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		final String useFileAsString = jobDataMapExtension.getJobDataMapStringValue(map, "useFile", false);
		if (useFileAsString != null) {
			logger.info("Given 'useFile' parameter is useFile={}", useFileAsString);
			useFile = Boolean.parseBoolean(useFileAsString);
		}else{
			useFile = userTaggingTaskPersistenceService.isMonitorFileDaily();
		}

		// ID for deployment wizard user tagging results
		resultsId = jobDataMapExtension.getJobDataMapStringValue(map, "resultsId", false);
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 1;
	}

	@Override
	protected boolean shouldReportDataReceived() {
		return false;
	}

	@Override
	protected void runSteps() throws Exception {
		Map<String, Long> taggedUsersCountBeforeRun = userService.groupByTags();

		if (useFile) {
			logger.info("Updating user tags from file {}.", customTagFilePath);
			try {
				updateFromFile();
			} catch (IOException e) {
				saveResultFailed();
				throw new JobExecutionException(e);
			}
		}

		logger.info("Updating user tags from mongo");
		userTagService.update();
		Map<String, Long> taggedUsersCountAfterRun = userService.groupByTags();

		saveResult(true, taggedUsersCountBeforeRun, taggedUsersCountAfterRun);
	}

	private void saveResultFailed(){
		saveResult(false, null, null);
	}

	private void saveResult(boolean success, Map<String, Long> taggedUsersCountBeforeRun, Map<String, Long> taggedUsersCountAfterRun) {
		if (resultsId != null) {
			logger.info("Saving the user tagging result to id {}", resultsId);

			Map<String, Long> deltaPerTag = null;

			if (taggedUsersCountAfterRun != null && taggedUsersCountBeforeRun != null) {
				 deltaPerTag = findDelta(taggedUsersCountBeforeRun, taggedUsersCountAfterRun);
			}

			userTaggingTaskPersistenceService.writeTaskResults(UserTaggingTaskPersistencyServiceImpl.RESULTS_KEY_NAME,
					resultsId, success, deltaPerTag);
		}
	}

	/**
	 * Calculates the number of users per tag that where affected by the tagging job
	 * @param taggedUsersCountBeforeRun
	 * @param taggedUsersCountAfterRun
	 * @return <tagName, usersAffected></tagName>
	 */
	protected static Map<String, Long> findDelta(Map<String, Long> taggedUsersCountBeforeRun, Map<String, Long> taggedUsersCountAfterRun) {
		Map<String, Long> changedUsers = new HashMap<>();

		if (taggedUsersCountAfterRun != null && taggedUsersCountBeforeRun != null) {
			// Get all the relevant tags
			Set<String> allTags = (new HashSet<>(taggedUsersCountAfterRun.keySet()));
			allTags.addAll(taggedUsersCountBeforeRun.keySet());

			// For each tag find the delta
			for (String tag : allTags) {
				Long usersBefore = taggedUsersCountBeforeRun.get(tag);
				Long usersAfter = taggedUsersCountAfterRun.get(tag);
				Long delta;

				if (usersAfter == null){
					usersAfter = 0l;
				}
				if (usersBefore == null){
					usersBefore = 0l;
				}

				// calculate the delta
				delta = Math.abs(Math.addExact(usersAfter, -usersBefore));

				// If changed
				if (delta != 0) {
					changedUsers.put(tag, delta);
				}
			}
		}
		return changedUsers;
	}


	private void updateFromFile() throws IOException, JobExecutionException {
		if (StringUtils.isEmpty(customTagFilePath)) {
			logger.error("Job failed. Empty customTagFilePath.");
			saveResultFailed();
			return;
		}
		File tagsFile = new File(customTagFilePath);

		//read the custom tag list and update the possible tags to add in the system
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			for (String line : FileUtils.readLines(tagsFile)) {
				final String[] splitLine = line.split(CSV_DELIMITER);
				String userRegex;
				List<String> tags;
				try {
					userRegex = splitLine[INDEX_USER_REGEX];
					tags = Arrays.asList(splitLine[INDEX_TAGS].split(Pattern.quote(TAGS_DELIMITER)));
				} catch (Exception e) {
					final String errorMessage = String.format("Job failed. File %s format is invalid.", customTagFilePath);
					logger.error(errorMessage);
					throw new JobExecutionException(errorMessage, e);
				}

				try {
					userTagService.addUserTagsRegex(userRegex, tags);
				} catch (Exception e) {
					final String errorMessage = String.format("Job failed. File %s format is invalid.", customTagFilePath);
					logger.error(errorMessage, e);
					throw new JobExecutionException(e);
				}

			}
			logger.info("tags loaded");
		} else {
			saveResultFailed();
			logger.error("Custom tag list file not accessible in path {}", customTagFilePath);
		}
	}

}
