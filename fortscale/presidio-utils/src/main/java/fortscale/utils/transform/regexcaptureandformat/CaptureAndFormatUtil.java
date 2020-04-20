package fortscale.utils.transform.regexcaptureandformat;

import java.util.List;
import java.util.regex.Matcher;

import static org.springframework.util.CollectionUtils.isEmpty;

public class CaptureAndFormatUtil {

    public static String captureAndFormat(CaptureAndFormatConfiguration conf, String sourceValue){
        Matcher matcher = conf.getPattern().matcher(sourceValue);

        if (matcher.matches()) {
            Object[] args = getArgs(conf.getCapturingGroupConfigurations(), matcher);
            return String.format(conf.getFormat(), args);
        }

        return null;
    }

    private static Object[] getArgs(List<CapturingGroupConfiguration> capturingGroupConfigurations, Matcher matcher) {
        return isEmpty(capturingGroupConfigurations) ? null : capturingGroupConfigurations.stream()
                .map(capturingGroupConfiguration -> {
                    String group = matcher.group(capturingGroupConfiguration.getIndex());
                    CapturingGroupConfiguration.CaseFormat caseFormat = capturingGroupConfiguration.getCaseFormat();
                    return caseFormat == null ? group : caseFormat.convert(group);
                })
                .toArray(Object[]::new);
    }
}
