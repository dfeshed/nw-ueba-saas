package presidio.input.pre.processing.application;

import org.apache.commons.lang3.Validate;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import presidio.input.pre.processing.pre.processor.PreProcessor;

import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class PresidioInputPreProcessingService implements CommandMarker {
    private final Map<String, PreProcessor<?>> nameToPreProcessorMap;

    public PresidioInputPreProcessingService(Collection<PreProcessor<?>> preProcessors) {
        Validate.notEmpty(preProcessors, "preProcessors cannot be empty.");
        Validate.noNullElements(preProcessors, "preProcessors cannot contain null elements.");
        nameToPreProcessorMap = preProcessors.stream().collect(toMap(PreProcessor::getName, identity()));
    }

    @CliCommand(value = "run", help = "Run a pre-processing command.")
    public void run(
            @CliOption(
                    key = {"name"},
                    mandatory = true,
                    help = "The name of the pre-processor."
            ) final String name,
            @CliOption(
                    key = {"arguments"},
                    mandatory = true,
                    help = "The arguments to pass to the pre-processor."
            ) final String argumentsAsJsonString
    ) {
        nameToPreProcessorMap
                .computeIfAbsent(name, PresidioInputPreProcessingService::throwUnknownPreProcessorNameException)
                .preProcess(argumentsAsJsonString);
    }

    private static PreProcessor<?> throwUnknownPreProcessorNameException(String name) {
        throw new IllegalArgumentException(String.format("Pre-processor named %s does not exist.", name));
    }
}
