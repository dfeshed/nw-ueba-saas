package fortscale.collection.jobs.tagging;

import fortscale.collection.jobs.FortscaleJob;
import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AddCustomTags extends FortscaleJob{

	public static final int INDEX_NAME = 0;
	public static final int INDEX_DISPLAY_NAME = 1;
	public static final int INDEX_INDICATOR = 2;
	public static final int INDEX_RULES = 3;
	private static Logger logger = LoggerFactory.getLogger(AddCustomTags.class);

	private static final String CSV_DELIMITER = "|";
	private static final String RULES_DELIMITER = ";";

	@Autowired
	private TagService tagService;

	@Value("${user.list.custom_tags.path:}")
	private String tagFilePath;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {}

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
		if (StringUtils.isEmpty(tagFilePath)) {
			logger.error("Job failed. Empty tagFilePath.");
			return;
		}
		File tagsFile = new File(tagFilePath);

		//read the custom tag list and update the possible tags to add in the system
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			for (String line : FileUtils.readLines(tagsFile)) {
				final String[] splitLine = line.split(Pattern.quote(CSV_DELIMITER)); //because "|" has a special meaning in a regex
				String name = null;
				String displayName = null;
				try {
					name = splitLine[INDEX_NAME];
					displayName = splitLine[INDEX_DISPLAY_NAME];
				} catch (Exception e) {
					final String errorMessage = String.format("File %s format is invalid.", tagFilePath);
					logger.error(errorMessage);
					throw new JobExecutionException(errorMessage, e);
				}
				boolean createsIndicator = Boolean.parseBoolean(splitLine[INDEX_INDICATOR]);
				Tag tag = new Tag(name, displayName, createsIndicator, true,false);

				if (splitLine.length > INDEX_RULES) { //rules isn't mandatory so we check if we have something in the relevant index
					String rules = splitLine[INDEX_RULES];
					tag.setRules(Arrays.asList(rules.split(RULES_DELIMITER)));
				}

				if (tagService.addTag(tag)) {
					logger.info("adding tag {}", tag);
				} else {
					logger.warn("fail to add tag {}", tag);
				}
			}
			logger.info("tags loaded");
		} else {
			logger.error("Custom tag list file not accessible in path {}", tagFilePath);
		}
	}

}