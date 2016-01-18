package fortscale.services.configuration.gds.state.field;

/**
 * @author gils
 * 17/01/2016
 */
public abstract class BaseFieldMetadata {
    protected String fieldName;
    protected FieldType fieldType;
    protected boolean isInUse;
    protected boolean isAdditionalField;

    public BaseFieldMetadata(String fieldName, FieldType fieldType, boolean isInUse, boolean isAdditionalField) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isInUse = isInUse;
        this.isAdditionalField = isAdditionalField;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseFieldMetadata that = (BaseFieldMetadata) o;

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
