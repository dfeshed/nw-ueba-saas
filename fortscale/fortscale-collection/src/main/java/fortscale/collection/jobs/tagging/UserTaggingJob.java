package fortscale.collection.jobs.tagging;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.services.UserTagService;
import fortscale.utils.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap map = jobExecutionContext.getMergedJobDataMap();
		final String useFileAsString = jobDataMapExtension.getJobDataMapStringValue(map, "useFile", false);
		if (useFileAsString != null) {
			logger.info("Given 'useFile' parameter is useFile={}", useFileAsString);
			useFile = Boolean.parseBoolean(useFileAsString);
		}
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
		if (useFile) {
			logger.info("Updating user tags from file {}.", customTagFilePath);
			try {
				updateFromFile();
			} catch (IOException e) {
				throw new JobExecutionException(e);
			}
		}
		else {
			logger.info("Updating user tags from mongo");
			userTagService.update();
		}

	}




	private void updateFromFile() throws IOException, JobExecutionException {
		if (StringUtils.isEmpty(customTagFilePath)) {
			logger.error("Job failed. Empty customTagFilePath.");
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
			logger.error("Custom tag list file not accessible in path {}", customTagFilePath);
		}
	}

}
