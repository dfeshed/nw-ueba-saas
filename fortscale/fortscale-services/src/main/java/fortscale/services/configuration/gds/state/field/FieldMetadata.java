package fortscale.services.configuration.gds.state.field;

/**
 * @author gils
 * 17/01/2016
 */
public class FieldMetadata {
    private String fieldName;
    private FieldType fieldType;
    private boolean isInUse;
    private boolean isAdditionalField;
    private boolean isScoreField;



    public FieldMetadata(String fieldName, FieldType fieldType,  boolean isScoreField, boolean isAdditionalField, boolean isInUse
          ) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isInUse = isInUse;
        this.isAdditionalField = isAdditionalField;
        this.isScoreField = isScoreField;
    }

    public String getFieldName() {
        return fieldName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public boolean isAdditionalField() {
        return isAdditionalField;
    }

    public boolean isScoreField() {
        return isScoreField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldMetadata that = (FieldMetadata) o;

        return fieldName.equals(that.fieldName);
    }

    @Override
    public int hashCode() {
        return fieldName.hashCode();
    }

    @Override
    public String toString() {
        return "fieldName='" + fieldName + '\'' +
                ", fieldType=" + fieldType +
                ", isInUse=" + isInUse +
                ", isAdditionalField=" + isAdditionalField +
                '}';
    }
}
