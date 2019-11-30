package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.config.AutomationConf;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.data.processing.DataProcessingHelper;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.OutputDataProcessingManager;
import com.rsa.netwitness.presidio.automation.utils.common.TitlesPrinter;
import org.json.JSONException;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.Instant.parse;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static presidio.data.generators.utils.TimeUtils.calcDaysBack;


public class OutputRunPrepareData extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(OutputRunPrepareData.class.getName());

    private OutputDataProcessingManager dataProcessingHelper = new OutputDataProcessingManager();
    private Function<String, String> SMART_RECORD_CONF_NAMES = e -> e.concat("_hourly");
    private List<String> ENTITIES_TO_PROCESS = AutomationConf.CORE_ENTITIES_TO_PROCESS;

    @Parameters("historical_days_back")
    @BeforeClass
    public void beforeClass(@Optional("10") int historicalDaysBack) throws JSONException, InterruptedException {
        TitlesPrinter.printTitle(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack);
        LOGGER.info("SMART_RECORD_CONF_NAMES = ".concat(ENTITIES_TO_PROCESS.stream().map(SMART_RECORD_CONF_NAMES).collect(Collectors.joining(", "))));
        LOGGER.info("ENTITIES_TO_PROCESS = ".concat(String.join(", ", ENTITIES_TO_PROCESS)));

        List<List<? extends Callable<Integer>>> parallelTasksToExecute = Stream.of(

                processorRun(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z")),
                recalculateUserScore(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z")),

                processorRun(parse(calcDaysBack(2) + "T13:00:00.00Z"), parse(calcDaysBack(0) + "T00:00:00.00Z")),
                recalculateUserScore(parse(calcDaysBack(historicalDaysBack) + "T00:00:00.00Z"), parse(calcDaysBack(0) + "T00:00:00.00Z"))

        ).sequential().collect(toList());


        for (List<? extends Callable<Integer>> parallelTasks : parallelTasksToExecute) {
            LOGGER.info("********** Next cycle **********");
            ExecutorService executor = Executors.newWorkStealingPool();
            List<Future<Integer>> futures = executor.invokeAll(parallelTasks, 5, TimeUnit.MINUTES);
            List<Integer> results = futures.parallelStream().map(toIntResult).collect(toList());
            assertThat(results).containsOnly(0);
        }

        DataProcessingHelper.INSTANCE.saveDataPreparationFinishTime().output.forEach(System.out::println);
    }

    @BeforeMethod
    public void beforeName(Method method){
        String testName = method.getName();
        LOGGER.info("Start running test: " + testName);
    }

    @Test
    public void calcUserScore() {
        RestHelper restHelper = new RestHelper();
        PresidioUrl url = restHelper.alerts().url().withNoParameters();
        List<AlertsStoredRecord> alerts = restHelper.alerts().request().getAlerts(url);

        assertThat(alerts)
                .withFailMessage(url + "\nAlerts REST response is null or empty.")
                .isNotNull()
                .isNotEmpty();
    }




    private List<OutputDataProcessingManager.ProcessorRun> processorRun(Instant startDate, Instant endDate) {
            return ENTITIES_TO_PROCESS.stream()
                .map(entity -> dataProcessingHelper.processorRun(startDate, endDate, SMART_RECORD_CONF_NAMES.apply(entity), entity))
                .collect(toList());
    }

    private List<OutputDataProcessingManager.RecalculateUserScore> recalculateUserScore(Instant startDate, Instant endDate) {
        return ENTITIES_TO_PROCESS.stream()
                .map(entity -> dataProcessingHelper.recalculateUserScore(startDate, endDate, entity))
                .collect(toList());
    }



    private Function<Future<Integer>, Integer> toIntResult = future -> {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return -100;
    };
}