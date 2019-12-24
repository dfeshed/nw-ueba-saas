package com.rsa.netwitness.presidio.automation.test.data.processing;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.InputPreProcessingTestManager;
import com.rsa.netwitness.presidio.automation.utils.common.TitlesPrinter;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.CORE_SCHEMAS_TO_PROCESS;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class InputPreProcessingData extends AbstractTestNGSpringContextTests {
    private static Logger LOGGER = (Logger) LoggerFactory.getLogger(InputPreProcessingData.class);

    private final ImmutableList<String> tlsEntityTypes = ImmutableList.of("dstPort.name", "domain.name", "dstAsn.name",
            "sslSubject.name", "dstOrg.name", "ja3.name", "dstCountry.name");

    private InputPreProcessingTestManager testManager = new InputPreProcessingTestManager();
    private Instant startDate, endDate;
    private int tlsExitCode = 0;

    @Parameters({"historical_days_back", "anomaly_day_back"})
    @BeforeClass
    public void prepareTestData(@Optional("30") int historicalDaysBack, @Optional("1") int anomalyDay) throws InterruptedException {
        TitlesPrinter.printTitle(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay);
        endDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
        LOGGER.info("startDate=" + startDate + " endDate=" + endDate);
        LOGGER.info("CORE_SCHEMAS_TO_PROCESS = ".concat(String.join(", ", CORE_SCHEMAS_TO_PROCESS)));

        if (CORE_SCHEMAS_TO_PROCESS.contains("TLS")) {
            InputPreProcessingTestManager.PreProcessing task = testManager.inputTlsPreProcessing(startDate, endDate, tlsEntityTypes);
            ExecutorService executor = Executors.newWorkStealingPool();
            List<Future<Integer>> futures = executor.invokeAll(List.of(task), 3, TimeUnit.MINUTES);
            tlsExitCode = futures.parallelStream().map(toIntResult).collect(toList()).get(0);
        } else {
            LOGGER.info("TLS pre-processing is skipped. Reason: TLS schema processing is disabled.");
            tlsExitCode = 0;
        }
    }


    @Test
    public void tlsInputPreProcessing() {
        assertThat(tlsExitCode).as("Exit code").isEqualTo(0);
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