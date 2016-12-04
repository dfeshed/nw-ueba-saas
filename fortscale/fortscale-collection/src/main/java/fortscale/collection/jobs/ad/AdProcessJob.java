package fortscale.collection.jobs.ad;

import fortscale.collection.io.BufferedLineReader;
import fortscale.collection.jobs.FortscaleJob;
import fortscale.collection.morphlines.MorphlinesItemsProcessor;
import fortscale.collection.morphlines.RecordToStringItemsProcessor;
import fortscale.monitor.domain.JobDataReceived;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


public abstract class AdProcessJob extends FortscaleJob {
	private static Logger logger = Logger.getLogger(AdProcessJob.class);

	@Autowired
	private ApplicationConfigurationService applicationConfigurationService;

	@Autowired
	protected ImpalaClient impalaClient;
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Value("${collection.lines.print.skip}")
	protected int linesPrintSkip;
	@Value("${collection.lines.print.enabled}")
	protected boolean linesPrintEnabled;

	private static final String DELIMITER = "=";
	private static final String KEY_SUCCESS = "success";


	protected MorphlinesItemsProcessor morphline;
	protected RecordToStringItemsProcessor recordToString;

	// job parameters:
	private String ldiftocsv;
	protected String inputPath;
	protected String finishPath;
	protected String errorPath;

	private String filesFilter;

	@Autowired
	protected StatsService statsService;
	
	String[] outputFields;

	String outputSeparator;

	private String resultsKey;

	@Override
	protected void getJobParameters(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			Path file = Paths.get("/tmp/AD/ETL_JJPPPPrrr");
			List<String> lines = Collections.singletonList("vv");
			Files.write(file, lines, Charset.forName("UTF-8"));

			JobDataMap map = jobExecutionContext.getMergedJobDataMap();

			lines = Collections.singletonList("final JobKey key = jobExecutionContext.getJobDetail().getKey();");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			final JobKey key = jobExecutionContext.getJobDetail().getKey();

			// get parameters values from the job data map
			ldiftocsv = jobDataMapExtension.getJobDataMapStringValue(map, "ldiftocsv");
			lines = Collections.singletonList("ldiftocsv");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			inputPath = jobDataMapExtension.getJobDataMapStringValue(map, "inputPath");
			lines = Collections.singletonList("inputPath");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			finishPath = jobDataMapExtension.getJobDataMapStringValue(map, "finishPath");
			lines = Collections.singletonList("finishPath");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			errorPath = jobDataMapExtension.getJobDataMapStringValue(map, "errorPath");
			lines = Collections.singletonList("errorPath");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			filesFilter = jobDataMapExtension.getJobDataMapStringValue(map, "filesFilter");
			lines = Collections.singletonList("filesFilter");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			// build record to items processor
			outputFields = ImpalaParser.getTableFieldNamesAsArray(jobDataMapExtension.getJobDataMapStringValue(map, "outputFields"));
			lines = Collections.singletonList("outputFields");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			outputSeparator = jobDataMapExtension.getJobDataMapStringValue(map, "outputSeparator");
			lines = Collections.singletonList("outputSeparator");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			recordToString = new RecordToStringItemsProcessor(outputSeparator, statsService, "AdProcessJob", outputFields);
			lines = Collections.singletonList("recordToString");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			lines = Collections.singletonList(map.getWrappedMap().toString()); /**/
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND); /**/

			morphline = jobDataMapExtension.getMorphlinesItemsProcessor(map, "morphlineFile");
			lines = Collections.singletonList("morphline =");
			Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			// random generated ID for deployment wizard fetch and ETL results


				Path file4 = Paths.get("/tmp/AD/ETL_BBB_" + resultsKey);
				lines = Collections.singletonList("vv");
				Files.write(file4, lines, Charset.forName("UTF-8"));
			try {
				final String resultsId = jobDataMapExtension.getJobDataMapStringValue(map, "resultsId");
				if (resultsId != null) {
                    resultsKey = key.getName().toLowerCase() + "." + resultsId;
                }
			} catch (JobExecutionException e) {
				logger.info("No resultsId was given as param.");
			}

		} catch (JobExecutionException e) {
			List<String> lines = Collections.singletonList("vv");
			Path file = Paths.get("/tmp/AD/ETL_JJPPPP1111_error");
			try {
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IllegalArgumentException e) {
			List<String> lines = Collections.singletonList("vv");
			Path file = Paths.get("/tmp/AD/ETL_JJPPPP2222_error");
			try {
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int getTotalNumOfSteps() {
		return 4;
	}

	@Override
	protected void runSteps() throws Exception {
		try {
			Path file = Paths.get("/tmp/AD/ETL_" + resultsKey);
			Files.write(file, new ArrayList<>(Arrays.asList("starting etl")), Charset.forName("UTF-8"));
			// list files in chronological order
			startNewStep("List Files");
			File[] files = listFiles(inputPath, filesFilter);
			Files.write(file, new ArrayList<>(Arrays.asList("listed files")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			if (files.length == 0) {
				Files.write(file, new ArrayList<>(Arrays.asList("files length = 0")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

				finishStep();
                return;
            }

			if (files.length > 1) {
				Files.write(file, new ArrayList<>(Arrays.asList("file length >1")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

				logger.warn("moving old files to {}", finishPath);
                for (int i = 0; i < files.length - 1; i++) {
                    moveFileToFolder(files[i], finishPath);
                    logger.info("moving {} to {}", files[i], finishPath);
                    monitor.warn(getMonitorId(), getStepName(), String.format("moving old file %s to %s", files[i], finishPath));
                }
            }
			Files.write(file, new ArrayList<>(Arrays.asList("started finish step")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			finishStep();

			Files.write(file, new ArrayList<>(Arrays.asList("finished finish step")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			startNewStep("create hadoop file writer");
			// get hadoop file writer

			finishStep();


			try {
				Files.write(file, new ArrayList<>(Arrays.asList("running final step11", Arrays.toString(files))), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

				processFile(files[files.length - 1]);
				Files.write(file, new ArrayList<>(Arrays.asList("running final step333")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			} finally{
                morphline.close();
            }

			Files.write(file, new ArrayList<>(Arrays.asList("running final step22")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

			runFinalStep();
			Files.write(file, new ArrayList<>(Arrays.asList("running final step332423")), Charset.forName("UTF-8"), StandardOpenOption.APPEND);

		} catch (Exception e) {
			List<String> lines = Collections.singletonList(e.getLocalizedMessage());
			Path file = Paths.get("/tmp/AD/error_" + resultsKey);
			Files.write(file, lines, Charset.forName("UTF-8"));

			return;
		}

		List<String> lines = Collections.singletonList(String.format("Inserting status to application configuration in key %s", resultsKey));
		Path file = Paths.get("/tmp/AD/EETTTLLLL.txt");
		Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);

		if (resultsKey != null) {
			logger.debug("Inserting status to application configuration in key {}", resultsKey);
			applicationConfigurationService.insertConfigItem(resultsKey, KEY_SUCCESS + DELIMITER + Boolean.TRUE);
		}
	}
	
	protected void runFinalStep() throws Exception{
		//by default do nothing
	}
	
	
	protected void processFile(File file) throws Exception {
		startNewStep("process file");

		BufferedLineReader reader = null;
		Date runtime = new Date();
		try {
            logger.info("starting to process {}", file.getName());
            if(ldiftocsv == null){
                reader = new BufferedLineReader( new BufferedReader(new FileReader(file)));
                processFile(file, reader, runtime);
            }else{
                Process pr =  runCmd(null, ldiftocsv, file.getAbsolutePath());
                reader = new BufferedLineReader( new BufferedReader(new InputStreamReader(pr.getInputStream())));
                // transform events in file
                processFile(null, reader, runtime);

                if(pr.waitFor() != 0){
                    handleCmdFailure(pr, ldiftocsv);
                    throw new JobExecutionException(String.format("got error while running shell command %s", ldiftocsv));
                }
            }
            logger.info("finished processing {}", file.getName());

		} catch (Exception e) {
			moveFileToFolder(file, errorPath);
			logger.error("error processing files {}", file.getAbsolutePath(),  e);
			throw new JobExecutionException(String.format("error processing files %s", file.getAbsolutePath()), e);
		} finally{
			if(reader != null){
				reader.close();
			}
		}
		
		moveFileToFolder(file, finishPath);
		
		finishStep();
	}

	protected boolean processFile(File file, BufferedLineReader reader, Date runtime) throws Exception {
		if(isTimestampAlreadyProcessed(runtime)){
			logger.warn("the following runtime ({}) was already processed.", runtime);
			return false;
		}
		String runtimeString = impalaParser.formatTimeDate(runtime);
		String timestampepoch = Long.toString(impalaParser.getRuntime(runtime));

		long totalLines = 0;

		if (file != null) {
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			totalLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
			lnr.close();
		}
		long numOfLines = 0;
		
		String line = null;
		int counter = 0;
		while ((line = reader.readLine()) != null) {
			numOfLines++;
			Record record = morphlineProcessLine(line);
			if(record != null){
				if(updateDb(record)){
					counter++;
				}
			}
			if (linesPrintEnabled && numOfLines % linesPrintSkip == 0) {
				if (totalLines > 0) {
					logger.info("{}/{} lines processed - {}% done", numOfLines, totalLines,
							Math.round(((float) numOfLines / (float) totalLines) * 100));
				} else {
					logger.info("{} lines processed", numOfLines);
				}
			}
		}
		
		monitor.addDataReceived(getMonitorId(), new JobDataReceived(getDataRecievedType(), new Integer(counter), ""));
		if (reader.HasErrors()) {
			monitor.error(getMonitorId(), getStepName(), reader.getException().toString());
			return false;
		} else {
			if (reader.hasWarnings()) {
				monitor.warn(getMonitorId(), getStepName(), reader.getException().toString());
			}
			return true;
		}
	}
	
	protected abstract String getDataRecievedType();
	protected abstract boolean isTimestampAlreadyProcessed(Date runtime);
	protected abstract boolean updateDb(Record record) throws Exception;
	

	protected Record morphlineProcessLine(String line){
		return morphline.process(line, null);
	}

	protected String[] getOutputFields() {
		return outputFields;
	}
	
	@Override
	protected boolean shouldReportDataReceived() {
		return true;
	}


}
