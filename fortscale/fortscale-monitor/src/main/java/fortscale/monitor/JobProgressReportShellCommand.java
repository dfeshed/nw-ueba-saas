package fortscale.monitor;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/***
 * Shell command class to report on job progress. Use main method to call
 * directly from the command line to report on job progress.
 */
@Component
public class JobProgressReportShellCommand {

	private static Logger logger = LoggerFactory.getLogger(JobProgressReportShellCommand.class);
	
	@Autowired
	private JobProgressReporter reporter;
	
	public JobProgressReportShellCommand() {}
	
	public JobProgressReportShellCommand(JobProgressReporter reporter) {
		this.reporter = reporter;
	}
	
	/***
	 * Usage: 
 	 * -h,--help                                     prints help usage
  	 * -sj,--start-job <source-type> <job-name>      reports job start
	 * -fj,--finish-job <id>                         reports job finish
  	 * -ss,--start-step <id> <step-name> <ordinal>   reports step start
 	 * -fs,--finish-step <id> <step-name>>           reports step finish
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/monitor-context.xml");
		
		JobProgressReportShellCommand me = context.getBean(JobProgressReportShellCommand.class);
		me.run(args);
		
		context.close();
	}
	
	private static Options createOptions() {
		Options options = new Options();
		
		// help option
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("prints help usage");
		options.addOption(OptionBuilder.create("h"));
		
		// start job option
		OptionBuilder.withArgName("source-type> <job-name");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(2);
		OptionBuilder.withLongOpt("start-job");
		OptionBuilder.withDescription("reports job start");
		options.addOption(OptionBuilder.create("sj"));
		
		// finish job option
		OptionBuilder.withArgName("id");
		OptionBuilder.hasArg();
		OptionBuilder.withLongOpt("finish-job");
		OptionBuilder.withDescription("reports job finish");
		options.addOption(OptionBuilder.create("fj"));
		
		// start step option
		OptionBuilder.withArgName("id> <step-name> <ordinal");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(3);
		OptionBuilder.withLongOpt("start-step");
		OptionBuilder.withDescription("reports step start");
		options.addOption(OptionBuilder.create("ss"));
		
		// start step option
		OptionBuilder.withArgName("id> <step-name>");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(2);
		OptionBuilder.withLongOpt("finish-step");
		OptionBuilder.withDescription("reports step finish");
		options.addOption(OptionBuilder.create("fs"));
		
		return options;
	}

	private static void printUsage() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("JobProgressReportShellCommand", createOptions());
	}
	
	public void run(String[] args) {
		Options options = createOptions();
		PosixParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h") || cmd.getOptions().length!=1) {
				printUsage();
			}
			
			HandleCommand(cmd);
		}
		catch (ParseException e) {
			logger.error("error parsing command line", e);
		}
	}
	
	private void HandleCommand(CommandLine cmd) {
		
		if (cmd.hasOption("sj")) {
			// handle start job
			String[] args = cmd.getOptionValues("sj");
			if (args.length == 2) {
				String sourceType = args[0];
				String jobName = args[1];
				
				String id = reporter.startJob(sourceType, jobName);
				System.out.print(id);
			}
			
		} else if (cmd.hasOption("fj")) {
			// handle finish job
			String[] args = cmd.getOptionValues("fj");
			if (args.length == 1) {
				String id = args[0];
				
				reporter.finishJob(id);
			}
			
		} else if (cmd.hasOption("ss")) {
			// handle start step
			String[] args = cmd.getOptionValues("ss");
			if (args.length == 3) {
				String id = args[0];
				String stepName = args[1];
				int ordinal = 0;
				try {
					String ordinalArg = args[2];
					ordinal = Integer.parseInt(ordinalArg);
				} catch (NumberFormatException e) {}
				
				reporter.startStep(id, stepName, ordinal);
			}

			
		} else if (cmd.hasOption("fs")) {
			// handle finish step
			String[] args = cmd.getOptionValues("fs");
			if (args.length == 2) {
				String id = args[0];
				String stepName = args[1];
				
				reporter.finishStep(id, stepName);
			}
			
		} else {
			logger.error("no option matched in command line: " + cmd.toString());
		}
	}
	
}
