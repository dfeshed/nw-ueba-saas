package com.rsa.netwitness.presidio.automation.test_managers;

//TODO: resolve? TESTS SHOULD NOT DEPEND ON product packages, except SDKs!

import com.rsa.netwitness.presidio.automation.domain.config.Consts;
import com.rsa.netwitness.presidio.automation.ssh.TerminalCommandsSshUtils;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserter;
import com.rsa.netwitness.presidio.automation.utils.input.inserter.InputInserterFactory;
import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.data.domain.event.Event;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.time.Instant;
import java.util.List;

import static com.rsa.netwitness.presidio.automation.file.LogSshUtils.printLogIfError;
import static org.assertj.core.api.Assertions.assertThat;

public class InputTestManager {
    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    public static final String PRESIDIO_INPUT_APP = "java -Xms3072m -Xmx3072m -jar -Duser.timezone=UTC presidio-input-core.jar";

    private InputInserterFactory inserterFactory;

    public InputTestManager(InputInserterFactory inputInserterFactory) {
        this.inserterFactory = inputInserterFactory;
    }

    public void clear(String dataSource) throws Exception {
        // Clear raw events collection via input SDK
        presidioInputPersistencyService.cleanAll(Schema.valueOf(dataSource));
    }

    public int countAll(Instant startDate, Instant endDate, String dataSource) throws Exception {
        List<? extends AbstractAuditableDocument> events =
                presidioInputPersistencyService.find(Schema.valueOf(dataSource), startDate, endDate);
        return events.size();
    }

    /**
     * Prepares input for input component in MongoDB by calling inputSDK
     *
     * @param evList
     */
    public void insert(List<? extends Event> evList) {
        if (evList.isEmpty()) {
            return;
        }

        //currently insert expect all the events to be from the same type
        InputInserter inserter = inserterFactory.getInputInserter(evList.get(0).getClass());
        inserter.insert(evList);
    }

    /**
     * Calls Input jar as terminal command to process stored events
     *
     * @param startDate      - start of time interval for store
     * @param endDate        - start of time interval for store
     * @param dataSourceType - data source
     */
    public void process(Instant startDate, Instant endDate, String dataSourceType) {
        String logPath = "/tmp/presidio-input_run_" + dataSourceType + "_" + startDate.toString() + "_" + endDate.toString() + ".log";

        // process the data in the input_XXXXXX_raw_events collection
        SshResponse p = TerminalCommandsSshUtils.runCommand(PRESIDIO_INPUT_APP, true, Consts.PRESIDIO_DIR,
                "run", "--schema " + dataSourceType, "--start_date " + startDate, "--end_date " + endDate,
                "> " + logPath);

        printLogIfError(logPath);
        assertThat(p.exitCode)
                .withFailMessage("Error exit code. Log: " + logPath)
                .isEqualTo(0);
    }


}
