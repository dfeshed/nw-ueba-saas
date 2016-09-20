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

public class AddCustomTags extends FortscaleJob{

	private static Logger logger = LoggerFactory.getLogger(AddCustomTags.class);

	private static final String CSV_DELIMITER = ",";

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
		File tagsFile = new File(tagFilePath);
		//read the custom tag list and update the possible tags to add in the system
		if (StringUtils.isEmpty(tagFilePath)) {
			logger.error("No tag file found");
			return;
		}
		if (tagsFile.exists() && tagsFile.isFile() && tagsFile.canRead()) {
			for (String line : FileUtils.readLines(tagsFile)) {
				String name = line.split(CSV_DELIMITER)[0];
				String displayName = line.split(CSV_DELIMITER)[1];
				boolean createsIndicator = Boolean.parseBoolean(line.split(CSV_DELIMITER)[2]);
				if (tagService.addTag(new Tag(name, displayName, createsIndicator, true))) {
					logger.info("adding tag {}", line);
				} else {
					logger.warn("fail to add tag tag {}", line);
				}
			}
			logger.info("tags loaded");
		} else {
			logger.error("Custom tag list file not accessible in path {}", tagFilePath);
		}
	}

}