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
import org.springframework.util.StringUtils;

import fortscale.monitor.domain.JobDataReceived;

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
 	 * -h,--help                                     	prints help usage
  	 * -sj,--start-job <source-type> <job-name> <steps> reports job start
	 * -fj,--finish-job <id>                         	reports job finish
  	 * -ss,--start-step <id> <step-name> <ordinal>   	reports step start
 	 * -fs,--finish-step <id> <step-name>>           	reports step finish
 	 * -warn, --warning <id> <step-name> <message>	 	report warning during step
 	 * -err, --error <id> <step-name> <message>	     	report error during step
 	 * -data, --data-received <id> <data type> <value> <value type> report data received by the job
	 */
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/monitor-context.xml", 
				"classpath*:META-INF/spring/mongo-context.xml");
		
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
		OptionBuilder.withArgName("source-type> <job-name> <num-steps");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(4);
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
		
		// error option
		OptionBuilder.withArgName("id> <step-name> <message");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(2);
		OptionBuilder.withLongOpt("error");
		OptionBuilder.withDescription("report error during step");
		options.addOption(OptionBuilder.create("err"));

		// error option
		OptionBuilder.withArgName("id> <step-name> <message");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(2);
		OptionBuilder.withLongOpt("warning");
		OptionBuilder.withDescription("report warning during step");
		options.addOption(OptionBuilder.create("warn"));
		
		// add data received
		OptionBuilder.withArgName("id> <data type> <value> <value type");
		OptionBuilder.withValueSeparator(' ');
		OptionBuilder.hasArgs(4);
		OptionBuilder.withLongOpt("data-received");
		OptionBuilder.withDescription("reports the data received");
		options.addOption(OptionBuilder.create("data"));
		
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
			if (args.length == 4) {
				try {
					String sourceType = args[0];
					String jobName = args[1];
					int numSteps = Integer.parseInt(args[2]);
					boolean shouldReportData = Boolean.parseBoolean(args[3]);
					
					String id = reporter.startJob(sourceType, jobName, numSteps, shouldReportData);
					System.out.print(id);
				} catch (NumberFormatException e) {}
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
			
		} else if (cmd.hasOption("err")) {
			//handle error in step
			String[] args = cmd.getOptionValues("err");
			if (args.length == 2) {
				String id = args[0];
				String stepName = args[1];
				
				// get the message from the rest of the command args
				String message = StringUtils.arrayToDelimitedString(cmd.getArgs(), " ");
				
				reporter.error(id, stepName, message);
			}
			
		} else if (cmd.hasOption("warn")) {
			// handle warn in step
			String[] args = cmd.getOptionValues("warn");
			if (args.length == 2) {
				String id = args[0];
				String stepName = args[1];
				
				// get the message from the rest of the command args
				String message = StringUtils.arrayToDelimitedString(cmd.getArgs(), " ");
				
				reporter.warn(id, stepName, message);
			}
			
		} else if (cmd.hasOption("data")) {
			// handle data received
			String[] args = cmd.getOptionValues("data");
			if (args.length == 4) {
				try {
					String id = args[0];
					String dataType = args[1];
					int value = Integer.parseInt(args[2]);
					String valueType = args[3];
						
					reporter.addDataReceived(id, new JobDataReceived(dataType, value, valueType));
				} catch (NumberFormatException e) {}
			}
			
		} else {
			logger.error("no option matched in command line: " + cmd.toString());
		}
	}
	
}
