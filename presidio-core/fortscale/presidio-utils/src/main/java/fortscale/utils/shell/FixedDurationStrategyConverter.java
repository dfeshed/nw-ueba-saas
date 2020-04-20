package fortscale.utils.shell;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.logging.Logger;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converter from String to FixedDurationStrategy object, to be used by Spring Shell
 */
@Component
public class FixedDurationStrategyConverter implements Converter<FixedDurationStrategy> {

    private static final Logger logger = Logger.getLogger(FixedDurationStrategyConverter.class);


    @Override
    public FixedDurationStrategy convertFromText(String text, Class<?> requiredType, String optionContext) {

        long parseLong;
        try {
            parseLong = Long.parseLong(text);
        } catch (NumberFormatException e) {
            logger.error(String.format("Invalid duration format for shell parameter: %s.", text), e);
            throw new IllegalArgumentException("Could not parse long.", e);
        }

        return FixedDurationStrategy.fromSeconds(parseLong);
    }

    @Override
    public boolean supports(Class<?> requiredType, String optionContext) {
        return FixedDurationStrategy.class.isAssignableFrom(requiredType);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> requiredType, String existingData, String optionContext, MethodTarget methodTarget) {
        return false;
    }
}
