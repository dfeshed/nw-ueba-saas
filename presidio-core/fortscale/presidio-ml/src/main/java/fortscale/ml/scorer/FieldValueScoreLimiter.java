package fortscale.ml.scorer;

import java.util.Map;

/*
 * A class representing a field limiting the reference score,
 * the limiting values and the corresponding maximum scores.
 */
public final class FieldValueScoreLimiter {
    private String fieldName;
    private Map<String, Integer> valueToMaxScoreMap;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Map<String, Integer> getValueToMaxScoreMap() {
        return valueToMaxScoreMap;
    }

    public void setValueToMaxScoreMap(Map<String, Integer> valueToMaxScoreMap) {
        this.valueToMaxScoreMap = valueToMaxScoreMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldValueScoreLimiter that = (FieldValueScoreLimiter) o;
        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null)
            return false;
        if (valueToMaxScoreMap != null ? !valueToMaxScoreMap.equals(that.valueToMaxScoreMap) : that.valueToMaxScoreMap != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (valueToMaxScoreMap != null ? valueToMaxScoreMap.hashCode() : 0);
        return result;
    }
}
