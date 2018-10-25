package fortscale.utils.recordreader.transformation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JoinerTransformation implements Transformation<String> {
    private String featureName;
    private List<String> requiredFieldNames;
    private String delimiter;

    /**
     * C'tor.
     *
     * @param featureName         the name of the new epochtime feature
     * @param requiredFieldNames    the names of the fields that will contain the joined values
     * @param delimiter the join delimiter
     */
    public JoinerTransformation(String featureName, List<String> requiredFieldNames, String delimiter) {
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
        boolean isMissingValue = requiredFieldNames.stream().anyMatch(fieldName -> requiredFieldNameToValueMap.get(fieldName) == null);
        if(isMissingValue){
            return null;
        }

        return requiredFieldNames.stream().map(fieldName -> requiredFieldNameToValueMap.get(fieldName).toString()).collect(Collectors.joining(delimiter));
    }
}