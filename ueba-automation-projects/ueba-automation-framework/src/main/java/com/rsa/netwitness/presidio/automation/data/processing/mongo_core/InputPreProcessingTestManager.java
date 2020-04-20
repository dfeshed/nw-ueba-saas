package com.rsa.netwitness.presidio.automation.data.processing.mongo_core;

import ch.qos.logback.classic.Logger;
import com.rsa.netwitness.presidio.automation.ssh.client.SshResponse;
import com.rsa.netwitness.presidio.automation.ssh.helper.SshHelper;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.rsa.netwitness.presidio.automation.domain.config.Consts.PRESIDIO_DIR;
import static com.rsa.netwitness.presidio.automation.file.LogSshUtils.printLogIfError;
import static org.assertj.core.api.Assertions.assertThat;

public class InputPreProcessingTestManager {
    static Logger LOGGER = (Logger) LoggerFactory.getLogger(InputPreProcessingTestManager.class);

    private static final String PRE_PROCESSING_APP_RUN = "java -Xms512m -Xmx512m -Duser.timezone=UTC --add-opens " +
            "java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED " +
            "--add-opens java.base/java.util.regex=ALL-UNNAMED -cp /var/lib/netwitness/presidio/batch/presidio-input-pre-processing.jar " +
            "-Dloader.main=presidio.input.pre.processing.application.PresidioInputPreProcessingApplication " +
            "org.springframework.boot.loader.PropertiesLauncher run";

    private static final String APPEND_NAME_LAST_OCCURRENCE_INSTANT = " --name last_occurrence_instant";
    private SshHelper sshHelper = new SshHelper();


    public PreProcessing inputTlsPreProcessing(Instant startInstant, Instant endInstant, List<String> entityTypes) {
        return new PreProcessing(startInstant, endInstant, "TLS", entityTypes);
    }


    public class PreProcessing implements Callable<Integer> {

        private final Instant startInstant;
        private final Instant endInstant;
        private final String schema;
        private final List<String> entityTypes;


        PreProcessing(Instant startInstant, Instant endInstant, String schema, List<String> entityTypes) {
            this.startInstant = startInstant;
            this.endInstant = endInstant;
            this.schema = schema;
            this.entityTypes = entityTypes;
        }

        @Override
        public Integer call() {
            LOGGER.info("Started input pre processing for " + String.join(", ", entityTypes));

            String logPath = "/tmp/" + schema  + "-pre-processing_" + startInstant.toString() + "_" + endInstant.toString() + ".log";
            String CMD = PRE_PROCESSING_APP_RUN + APPEND_NAME_LAST_OCCURRENCE_INSTANT + appendArguments();
            SshResponse response = sshHelper.uebaHostExec().setUserDir(PRESIDIO_DIR).run(CMD, " > " + logPath);

            printLogIfError(logPath);
            assertThat(response.exitCode)
                    .as("Error exit code. Log: " + logPath)
                    .isEqualTo(0);

            LOGGER.info("PreProcessing[" + schema + "] completed successfully.");
            return response.exitCode;
        }

        private String appendArguments() {
            final String fields = entityTypes.stream().map(e -> "\\\\\\\""+ e +"\\\\\\\"").collect(Collectors.joining(", "));

            return " --arguments \\\"{\\\\\\\"startInstant\\\\\\\": \\\\\\\"" + startInstant.toString() + "\\\\\\\", " +
                    "\\\\\\\"entityTypes\\\\\\\": [" + fields +"],  " +
                    "\\\\\\\"endInstant\\\\\\\": \\\\\\\"" + endInstant.toString() + "\\\\\\\", " +
                    "\\\\\\\"schema\\\\\\\": \\\\\\\"" + schema.toUpperCase() + "\\\\\\\"}\\\"";
        }
    }



}
