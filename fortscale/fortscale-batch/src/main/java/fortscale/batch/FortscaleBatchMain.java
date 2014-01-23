package fortscale.batch;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import fortscale.utils.logging.Logger;

@SuppressWarnings("static-access")
public class FortscaleBatchMain {
	private static Logger logger = Logger.getLogger(FortscaleBatchMain.class);

	// gather config files
	private static String[] configs = { "classpath*:META-INF/spring/fortscale-batch-context.xml" };

	private static final String UPDATE_AD_INFO = "updateAdInfo";
	private static final String UPDATE_AUTH_SCORE = "updateAuthScore";
	private static final String UPDATE_SSH_SCORE = "updateSshScore";
	private static final String UPDATE_VPN_SCORE = "updateVpnScore";
	private static final String UPDATE_GROUP_MEMBERSHIP_SCORE = "updateGroupMembershipScore";

	
	private static final Option OPT_BATCH_ACTION = OptionBuilder.isRequired()
			.hasArg().withArgName("STRING").withDescription("The batch action")
			.withLongOpt("batch_action").create("act");
	private static final Option OPT_BATCH_SCORE_FULL_PATH_LOCAL_FILE_NAME = OptionBuilder
			.hasArg()
			.withArgName("STRING")
			.withDescription(
					"The local file to which the score should be written")
			.withLongOpt("score_file").create("sf");
	private static final Option OPT_BATCH_TOTAL_SCORE_FULL_PATH_LOCAL_FILE_NAME = OptionBuilder
			.hasArg()
			.withArgName("STRING")
			.withDescription(
					"The local file to which the total score should be written")
			.withLongOpt("total_score_file").create("tsf");
	private static final Option OPT_HELP = OptionBuilder
			.withDescription("print help message").withLongOpt("help")
			.create("h");

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(OPT_BATCH_ACTION);
		options.addOption(OPT_BATCH_SCORE_FULL_PATH_LOCAL_FILE_NAME);
		options.addOption(OPT_BATCH_TOTAL_SCORE_FULL_PATH_LOCAL_FILE_NAME);
		options.addOption(OPT_HELP);

		return options;
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(120);
		formatter.printHelp("FortscaleBatchMain", createOptions());
	}

	public static void main(String[] args){
		// logger.debug("running parameters: " + StringUtils.join(args, ','));
		Options options = createOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(OPT_HELP.getOpt())
					|| line.getOptions().length == 0) {
				printUsage();
				System.exit(1);
			} else {
				run(line);
			}
		} catch (Exception exp) {
			// oops, something went wrong
			logger.error("got the following exception:", exp);
		}
	}

	public static void run(CommandLine line) throws Exception {
		String action = line.getOptionValue(OPT_BATCH_ACTION.getOpt());

		
		// create the app context
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(configs);
		

		FortscaleBatch fortscaleBatch = (FortscaleBatch) ctx
				.getBean("fortscaleBatch");
		if (action.equalsIgnoreCase(UPDATE_AD_INFO)) {
			fortscaleBatch.updateAdInfo();
		} else {
			String userTotalScoreCsvFileFullPathString = line.getOptionValue(OPT_BATCH_TOTAL_SCORE_FULL_PATH_LOCAL_FILE_NAME.getOpt());
			if (action.equalsIgnoreCase(UPDATE_GROUP_MEMBERSHIP_SCORE)) {
				String userAdScoreCsvFileFullPathString = line.getOptionValue(OPT_BATCH_SCORE_FULL_PATH_LOCAL_FILE_NAME.getOpt());
				fortscaleBatch.updateGroupMembershipScore(userAdScoreCsvFileFullPathString, userTotalScoreCsvFileFullPathString);
			} else if (action.equalsIgnoreCase(UPDATE_AUTH_SCORE)) {
				fortscaleBatch.updateAuthScore(userTotalScoreCsvFileFullPathString);
			}  else if (action.equalsIgnoreCase(UPDATE_SSH_SCORE)) {
				fortscaleBatch.updateSshScore(userTotalScoreCsvFileFullPathString);
			} else if (action.equalsIgnoreCase(UPDATE_VPN_SCORE)) {
				fortscaleBatch.updateVpnScore(userTotalScoreCsvFileFullPathString);
			} else {
				logger.error("no such action: {}.", action);
			}
		}
	}

}
