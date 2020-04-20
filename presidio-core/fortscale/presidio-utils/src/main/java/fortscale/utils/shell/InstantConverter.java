package fortscale.utils.shell;

import fortscale.utils.logging.Logger;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

/**
 * Converter from String to Instant object, to be used by Spring Shell
 * Created by efratn on 19/06/2017.
 */
@Component
public class InstantConverter implements Converter<Instant> {

    private static final Logger logger = Logger.getLogger(InstantConverter.class);

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    private final DateFormat dateFormat;


    public InstantConverter() {
        this.dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    }

    public InstantConverter(final DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public Instant convertFromText(String text, Class<?> requiredType, String optionContext) {
        try {
            dateFormat.parse(text);
        } catch (ParseException e) {
            logger.error("Invalid date format for shell parameter: {}. Expected format: {}", text, DEFAULT_DATE_FORMAT);
            throw new IllegalArgumentException("Could not parse date: " + e.getMessage());
        }

        return Instant.parse(text);
    }

    @Override
    public boolean supports(Class<?> requiredType, String optionContext) {
        return Instant.class.isAssignableFrom(requiredType);
    }

    @Override
    public boolean getAllPossibleValues(List<Completion> completions, Class<?> requiredType, String existingData, String optionContext, MethodTarget methodTarget) {
        return false;
    }
}
