package fortscale.ml.scorer.algorithm;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * An abstract class providing util functions used for testing and researching real data scenarios.
 * In order to generate the data, run get_ssh_research_data.bash (from research project:
 * https://bitbucket.org/fortscale/research).
 * Don't forget to change "PRINT_GRAPHS = true".
 */
@RunWith(JUnit4.class)
public abstract class AbstractScorerTest {
	private static final boolean PRINT_GRAPHS = false;

	private static boolean printingOffOverride = false;

	protected static void turnOffPrinting() {
		printingOffOverride = true;
	}

	protected static void revertPrinting() {
		printingOffOverride = false;
	}

	protected static void print(Object msg) {
		if (PRINT_GRAPHS && !printingOffOverride) {
			System.out.print(msg);
		}
	}

	protected static void println(Object msg) {
		print(msg + "\n");
	}

	protected static void println() {
		println("");
	}

	protected static class TestEventsBatch {
		public int num_of_events;
		public long time_bucket;
		public String normalized_src_machine;
		public String normalized_dst_machine;

		@Override
		public String toString() {
			return num_of_events + "," + normalized_src_machine + "," + normalized_dst_machine + "," + time_bucket + " (" + getFormattedDate(time_bucket) + ")";
		}

		public String getFeature() {
			return normalized_src_machine;
		}
	}

	private String getAbsoluteFilePath(String fileName) throws FileNotFoundException {
		URL fileURL = getClass().getClassLoader().getResource(fileName);
		if (fileURL == null) {
			throw new FileNotFoundException("file " + fileName + " not exist");
		}
		return fileURL.getFile();
	}

	private List<TestEventsBatch> readEventsFromCsv(String csvFileName) throws IOException {
		File csvFile = new File(getAbsoluteFilePath(csvFileName));
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(',');
		MappingIterator<TestEventsBatch> it = new CsvMapper().reader(TestEventsBatch.class).with(schema).readValues(csvFile);
		List<TestEventsBatch> res = new ArrayList<>();
		TestEventsBatch event = null;
		while (it.hasNext()) {
			try {
				event = it.next();
			}
			catch (RuntimeException e) {
				System.err.println("last event succussfully processed: " + event.toString());
				throw e;
			}
			if (!StringUtils.isBlank(event.getFeature())) {
				res.add(event);
			}
		}
		return res;
	}

	protected static class ScoredEvent {
		public int eventIndex;
		public TestEventsBatch event;
		public Double score;

		public ScoredEvent(int eventIndex, TestEventsBatch event, Double score) {
			this.eventIndex = eventIndex;
			this.event = event;
			this.score = score;
		}

		@Override
		public String toString() {
			return "#" + eventIndex + " " + event + " -> " + score;
		}
	}

	private static class ScenarioStats implements Comparable<ScenarioStats> {
		private Map<String, FeatureStats> featureToFeatureStats;
		private List<ScoredEvent> scoredEvents;
		private int numOfProcessedEvents;

		public ScenarioStats() {
			featureToFeatureStats = new HashMap<>();
			scoredEvents = new ArrayList<>();
			numOfProcessedEvents = 0;
		}

		public void addEventInfo(ScoredEvent scoredEvent, boolean isScoreInteresting) {
			numOfProcessedEvents++;
			FeatureStats featureStats = featureToFeatureStats.get(scoredEvent.event.getFeature());
			if (featureStats == null) {
				featureStats = new FeatureStats();
				featureToFeatureStats.put(scoredEvent.event.getFeature(), featureStats);
			}
			featureStats.addEventInfo(scoredEvent.event.time_bucket, numOfProcessedEvents);
			if (isScoreInteresting) {
				scoredEvents.add(scoredEvent);
			}
		}

		@Override
		public String toString() {
			if (scoredEvents.isEmpty()) {
				return "";
			}

			StringBuilder sb = new StringBuilder();
			sb.append("first time events:\n");
			Consumer<FeatureStats> printFeatureStats = featureStats ->
					sb.append(String.format("\t#%-8d %s\n", featureStats.firstEventIndex, getFormattedDate(featureStats.firstEventTime)));
			featureToFeatureStats.values().stream()
					.sorted((featureStats1, featureStats2) -> (int) Math.signum(featureStats1.firstEventTime - featureStats2.firstEventTime))
					.forEach(printFeatureStats);
			return sb.toString();
		}

		@Override
		public int compareTo(ScenarioStats o) {
			return numOfProcessedEvents - o.numOfProcessedEvents;
		}
	}

	private static class FeatureStats {
		public Long firstEventTime;
		private int firstEventIndex;
		public Long lastEventTime;
		private int lastEventIndex;

		public FeatureStats() {
			firstEventTime = null;
			lastEventTime = null;
		}

		public void addEventInfo(long eventTime, int eventIndex) {
			if (firstEventTime == null) {
				firstEventTime = eventTime;
				firstEventIndex = eventIndex;
				lastEventTime = eventTime;
				lastEventIndex = eventIndex;
			} else {
				firstEventTime = Math.min(firstEventTime, eventTime);
				firstEventIndex = Math.min(firstEventIndex, eventIndex);
				lastEventTime = Math.max(lastEventTime, eventTime);
				lastEventIndex = Math.max(lastEventIndex, eventIndex);
			}
		}
	}

	/**
	 * Running a scenario requires to know how to handle the scenario's events (e.g. - how to score an event,
	 * how to print model-relevant data contained in the event).
	 * A class extending AbstractModelTest should provide an implementation.
	 */
	protected abstract class ScenarioCallbacks {
		/**
		 * A callback called just before starting to run a scenario.
		 * Any initialization of state data used through the scenario run can be done here.
		 */
		public abstract void onScenarioRunStart();

		/**
		 * Give a score to the given event.
		 * @param eventsBatch data about the event (note that although the events batch class is used - which contains
		 *                    the number of events in the batch - only one event of the batch is to be scored).
		 * @return the event score.
		 */
		public abstract Double onScore(TestEventsBatch eventsBatch);

		/**
		 * Print info about the event.
		 * @param scoredEvent the event's data and score (given by onScore).
		 */
		public void onPrintEvent(ScoredEvent scoredEvent) {
			println(scoredEvent);
		}

		/**
		 * Called when finished processing the given event.
		 * Any maintenance of state data used through the scenario run can be done here.
		 * @param eventsBatch the event which has been processed.
		 */
		public abstract void onFinishProcessEvent(TestEventsBatch eventsBatch);
	}

	/**
	 * Run a real data scenario.
	 * The first 90% of the events won't be scored (they are only used for building the model).
	 * @param scenarioCallbacks callbacks needed when running the scenario.
	 * @param scenarioInfo all the info about the scenario needed in order to run it.
	 * @param minDate events occurring before this time won't be scored.
	 * @param minInterestingScore scores smaller than this number are considered not interesting, and won't be included in the result.
	 * @param printContextInfo if true, context info will be printed (aids in understanding the result scores).
	 * @return statistics about the result of running the scenario.
	 * @throws IOException
	 */
	private ScenarioStats runRealScenario(ScenarioCallbacks scenarioCallbacks, ScenarioInfo scenarioInfo, int minDate, int minInterestingScore, boolean printContextInfo) throws IOException {
		scenarioCallbacks.onScenarioRunStart();
		ScenarioStats scenarioStats = new ScenarioStats();
		int eventIndex = 0;
		for (final TestEventsBatch eventsBatch : scenarioInfo.eventsBatches) {
			for (int i = 0; i < eventsBatch.num_of_events; i++) {
				Double score = scenarioCallbacks.onScore(eventsBatch);
				boolean isScoreInteresting = eventsBatch.time_bucket >= minDate && score != null && score > minInterestingScore;
				ScoredEvent scoredEvent = new ScoredEvent(eventIndex++, eventsBatch, score);
				scenarioStats.addEventInfo(scoredEvent, isScoreInteresting);
				if (isScoreInteresting && printContextInfo) {
					scenarioCallbacks.onPrintEvent(scoredEvent);
				}
				scenarioCallbacks.onFinishProcessEvent(eventsBatch);
			}
		}
		if (printContextInfo) {
			println(scenarioStats);
		}
		return scenarioStats;
	}

	protected static String getFormattedDate(long date) {
		return new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(new Date(TimestampUtils.convertToMilliSeconds(date)));
	}

	private class ScenarioInfo {
		public String filePath;
		private List<TestEventsBatch> eventsBatches;
		public int numOfEvents;
		public Long firstEventTime;
		public Long lastEventTime;

		public ScenarioInfo(String filePath) throws IOException {
			this.filePath = filePath;
			eventsBatches = readEventsFromCsv(filePath);
			numOfEvents = 0;
			firstEventTime = null;
			lastEventTime = null;
			if (eventsBatches != null) {
				for (TestEventsBatch eventsBatch : eventsBatches) {
					numOfEvents += eventsBatch.num_of_events;
				}
				if (!eventsBatches.isEmpty()) {
					firstEventTime = eventsBatches.get(0).time_bucket;
					lastEventTime = eventsBatches.get(eventsBatches.size() - 1).time_bucket;
				}
			}
		}
	}

	protected void printGoogleSheetsExplaination(String googleSheetName) {
		println("\n\n\nCopy the following output into \"" + googleSheetName + "\" sheet in the following URL: https://docs.google.com/spreadsheets/d/1eNqu2K3mIUCH3b-NXeQM5VqBkcaEqwcSxiNWZ-FzdHg/edit#gid=1047563136&vpid=A1\n");
	}

	/**
	 * Given the results of running a real data scenario, print a graph of the results.
	 * @param googleSheetName the name of the google sheet which is capable of displaying the results of this function.
	 * @param scoredEvents the result of running a real data scenario.
	 */
	private void printRealScenarioGraph(String googleSheetName, List<ScoredEvent> scoredEvents) {
		printGoogleSheetsExplaination("real-scenario-" + googleSheetName);
		List<String> featureValues = scoredEvents.stream().map(scoredEvent -> scoredEvent.event.getFeature()).distinct().collect(Collectors.toList());
		for (String featureValue : featureValues) {
			print((StringUtils.isBlank(featureValue) ? "(empty string)" : featureValue) + "\t");
		}
		println();
		String tabs = "";
		for (int i = 0; i < featureValues.size(); i++) {
			tabs += "\t";
		}
		for (ScoredEvent scoredEvent : scoredEvents) {
			int featureValueIndex = featureValues.indexOf(scoredEvent.event.getFeature());
			print(tabs.substring(0, featureValueIndex));
			print("" + scoredEvent.score);
			print(tabs.substring(0, featureValues.size() - featureValueIndex));
			println();
		}
	}

	private static final String SSH_REAL_DATA_PATH = "ssh-real-data";

	/**
	 * Run a real data scenario and print interesting stuff.
	 * @param scenarioCallbacks callbacks needed when running the scenario.
	 * @param fileName the name of the csv file containing the scenario's events.
	 * @param minInterestingScore scores smaller than this number are considered not interesting, and won't be included in the printed result.
	 */
	protected void runAndPrintRealScenario(ScenarioCallbacks scenarioCallbacks, String fileName, int minInterestingScore) throws IOException {
		ScenarioInfo scenarioInfo = new ScenarioInfo(SSH_REAL_DATA_PATH + "/" + fileName);
		ScenarioStats scenarioStats = runRealScenario(scenarioCallbacks, scenarioInfo, (int) (scenarioInfo.firstEventTime + (scenarioInfo.lastEventTime - scenarioInfo.firstEventTime) * 0.9), minInterestingScore, true);
		printRealScenarioGraph(scenarioInfo.filePath.substring(0, scenarioInfo.filePath.indexOf('/')), scenarioStats.scoredEvents);
	}

	private static class UsersStatistics {
		public int numOfRegularUsers;
		public Map<String, ScenarioStats> anomalousUserScenarioToScenarioStats;

		public UsersStatistics() {
			this.numOfRegularUsers = 0;
			this.anomalousUserScenarioToScenarioStats = new HashMap<>();
		}

		public int getNumOfRegularUsers() {
			return numOfRegularUsers;
		}

		public int getNumOfAnomalousUsers() {
			return anomalousUserScenarioToScenarioStats.size();
		}
	}

	private class ScenariosInfo {
		public Long firstEventTime;
		public Long lastEventTime;

		private List<Map.Entry<ScenarioInfo, Integer>> sortedScenariosByNumOfEvents;

		public ScenariosInfo(String dirPath) throws IOException {
			List<ScenarioInfo> scenarioInfos = getScenarioInfos(dirPath);
			Map<ScenarioInfo, Integer> scenarioToNumOfEvents = new HashMap<>(scenarioInfos.size());
			if (scenarioInfos.isEmpty()) {
				firstEventTime = null;
				lastEventTime = null;
				sortedScenariosByNumOfEvents = new ArrayList<>();
			} else {
				firstEventTime = Long.MAX_VALUE;
				lastEventTime = Long.MIN_VALUE;
				for (ScenarioInfo scenarioInfo : scenarioInfos) {
					if (scenarioInfo.numOfEvents > 0) {
						firstEventTime = Math.min(firstEventTime, scenarioInfo.firstEventTime);
						lastEventTime = Math.max(lastEventTime, scenarioInfo.lastEventTime);
						scenarioToNumOfEvents.put(scenarioInfo, scenarioInfo.numOfEvents);
					}
				}

				sortedScenariosByNumOfEvents = sortMapByValues(scenarioToNumOfEvents);
			}
		}

		private List<ScenarioInfo> getScenarioInfos(String dirPath) throws IOException {
			File[] scenarioFiles = new File(dirPath).listFiles();
			List<ScenarioInfo> scenarioInfos = new ArrayList<>(scenarioFiles.length);
			for (File file : scenarioFiles) {
				scenarioInfos.add(new ScenarioInfo(SSH_REAL_DATA_PATH + "/" + file.getName()));
			}
			return scenarioInfos;
		}

		public int size() {
			return sortedScenariosByNumOfEvents.size();
		}

		public ScenarioInfo get(int i) {
			return sortedScenariosByNumOfEvents.get(i).getKey();
		}
	}

	/**
	 * Test multiple scenarios to see how many users are anomalous.
	 * @param scenarioCallbacks callbacks needed when running the scenarios.
	 * @param expectedPortionOfAnomalousUsers which portion of the users should be anomalous (used with assert).
	 * @param minInterestingScore scores smaller than this number are considered not interesting, and won't be included in the result.
	 * @param limitNumOfScenarios if there are more than limitNumOfScenarios scenarios, only the first limitNumOfScenarios will be run.
	 */
	protected void testRealScenariosHowManyAnomalousUsers(ScenarioCallbacks scenarioCallbacks, double expectedPortionOfAnomalousUsers, int minInterestingScore, int limitNumOfScenarios) throws IOException {
		ScenariosInfo scenariosInfo;
		try {
			scenariosInfo = new ScenariosInfo(getAbsoluteFilePath(SSH_REAL_DATA_PATH));
			limitNumOfScenarios = Math.min(limitNumOfScenarios, scenariosInfo.size());
		} catch (FileNotFoundException e) {
			println("directory not found");
			return;
		}
		int minDate = (int) (scenariosInfo.firstEventTime + (scenariosInfo.lastEventTime - scenariosInfo.firstEventTime) * 0.9);

		// run all the scenarios and create some statistics:
		Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics = new HashMap<>();
		for (int i = 0; i < limitNumOfScenarios; i++) {
			ScenarioInfo scenarioInfo = scenariosInfo.get(i);
			println("\nrunning scenario " + (i + 1) + " / " + limitNumOfScenarios + " with " + scenarioInfo.numOfEvents + " events: " + scenarioInfo.filePath + " (min date " + getFormattedDate(minDate) + ")");
			ScenarioStats scenarioStats = runRealScenario(scenarioCallbacks, scenarioInfo, minDate, minInterestingScore, true);

			int logNumOfEvents = (int) (Math.log(scenarioInfo.numOfEvents) / Math.log(10));
			UsersStatistics usersStatistics = logNumOfEventsToUsersStatistics.get(logNumOfEvents);
			if (usersStatistics == null) {
				usersStatistics = new UsersStatistics();
				logNumOfEventsToUsersStatistics.put(logNumOfEvents, usersStatistics);
			}
			if (!scenarioStats.scoredEvents.isEmpty()) {
				usersStatistics.anomalousUserScenarioToScenarioStats.put(scenarioInfo.filePath, scenarioStats);
			} else {
				usersStatistics.numOfRegularUsers++;
			}
		}

		// print interesting stuff about the results:
		printAnomalousUsersRatios(scenariosInfo, logNumOfEventsToUsersStatistics);

		// assert stuff
		int totalAnomalousUsers = getTotalAnomalousUsers(logNumOfEventsToUsersStatistics);
		Assert.assertEquals(expectedPortionOfAnomalousUsers, (double) totalAnomalousUsers / limitNumOfScenarios, 0.001);
	}

	/**
	 * Test multiple scenarios to see how many users are anomalous.
	 * @param scenarioCallbacks callbacks needed when running the scenarios.
	 * @param expectedPortionOfAnomalousUsers which portion of the users should be anomalous (used with assert).
	 * @param minInterestingScore scores smaller than this number are considered not interesting, and won't be included in the result.
	 */
	protected void testRealScenariosHowManyAnomalousUsers(ScenarioCallbacks scenarioCallbacks, double expectedPortionOfAnomalousUsers, int minInterestingScore) throws IOException {
		testRealScenariosHowManyAnomalousUsers(scenarioCallbacks, expectedPortionOfAnomalousUsers, minInterestingScore, Integer.MAX_VALUE);
	}

	private Integer getTotalAnomalousUsers(Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics) {
		return logNumOfEventsToUsersStatistics.entrySet().stream()
				.map((entry) -> entry.getValue().getNumOfAnomalousUsers())
				.reduce((numOfAnomalousUsers1, numOfAnomalousUsers2) -> numOfAnomalousUsers1 + numOfAnomalousUsers2)
				.get();
	}

	private void printAnomalousUsersRatios(ScenariosInfo scenariosInfo, Map<Integer, UsersStatistics> logNumOfEventsToUsersStatistics) {
		println(String.format("\n%s: <anomalous users / <total users> -> followed by a list of the anomalous users", StringUtils.rightPad("<number of events>", 20)));
		for (Map.Entry<Integer, UsersStatistics> entry : logNumOfEventsToUsersStatistics.entrySet()) {
			Integer logNumOfEvents = entry.getKey();
			UsersStatistics usersStatistics = entry.getValue();
			String logNumOfEventsRange = (int) Math.pow(10, logNumOfEvents) + " - " + (int) Math.pow(10, logNumOfEvents + 1);
			println(String.format("%s: %3.2f%%   %-6d / %-6d anomalous users",
					StringUtils.rightPad(logNumOfEventsRange, 20),
					100.0 * usersStatistics.getNumOfAnomalousUsers() / (usersStatistics.getNumOfRegularUsers() + usersStatistics.getNumOfAnomalousUsers()),
					usersStatistics.getNumOfAnomalousUsers(),
					usersStatistics.numOfRegularUsers + usersStatistics.getNumOfAnomalousUsers()));
			Optional<Integer> maxScenarioLength = usersStatistics.anomalousUserScenarioToScenarioStats.keySet().stream()
					.max((s1, s2) -> s1.length() - s2.length())
					.map(String::length);
			for (Map.Entry<String, ScenarioStats> e : sortMapByValues(usersStatistics.anomalousUserScenarioToScenarioStats)) {
				ScenarioStats scenarioStats = e.getValue();
				ScoredEvent maxScoredEvent = scenarioStats.scoredEvents.stream()
						.max((scoredEvent1, scoredEvent2) -> (int) Math.signum(scoredEvent1.score - scoredEvent2.score))
						.get();
				println(String.format("\t%-6d: %-" + maxScenarioLength.get() + "s   %s",
						scenarioStats.numOfProcessedEvents,
						e.getKey(),
						maxScoredEvent));
			}
		}
		println(String.format("\ntotal %d / %d anomalous users", getTotalAnomalousUsers(logNumOfEventsToUsersStatistics), scenariosInfo.size()));
	}

	private static <L, R extends Comparable> List<Map.Entry<L, R>> sortMapByValues(Map<L, R> m) {
		List<Map.Entry<L, R>> sortedList = new ArrayList<>(m.entrySet());
		Collections.sort(sortedList, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
		return sortedList;
	}
}
