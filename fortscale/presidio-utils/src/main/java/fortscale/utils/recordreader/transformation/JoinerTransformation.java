package fortscale.utils.recordreader.transformation;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class JoinerTransformation implements Transformation<String> {
    private String featureName;
    private Collection<String> requiredFieldNames;
    private String delimiter;

    /**
     * C'tor.
     *
     * @param featureName         the name of the new epochtime feature
     * @param instantFieldName    the name of the required instant field
     * @param resolutionInSeconds the resolution in seconds of the new epochtime feature
     */
    public JoinerTransformation(String featureName, Collection<String> requiredFieldNames, String delimiter) {
        this.featureName = featureName;
        this.requiredFieldNames = requiredFieldNames;
        this.delimiter = delimiter;
    }

    @Override
    public String getFeatureName() {
        return featureName;
    }

    @Override
    public Collection<String> getRequiredFieldNames() {
        return requiredFieldNames;
    }

    @Override
    public String transform(Map<String, Object> requiredFieldNameToValueMap) {
        boolean isMissingValue = requiredFieldNames.stream().filter(fieldName -> !requiredFieldNameToValueMap.containsKey(fieldName)).findFirst().isPresent();
        if(isMissingValue){
            return null;
        }

        return requiredFieldNames.stream().map(fieldName -> requiredFieldNameToValueMap.get(fieldName).toString()).collect(Collectors.joining(delimiter));
    }
}