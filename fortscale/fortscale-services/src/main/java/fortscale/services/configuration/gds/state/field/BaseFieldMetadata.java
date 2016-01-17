package fortscale.services.configuration.gds.state.field;

/**
 * @author gils
 * 17/01/2016
 */
public abstract class BaseFieldMetadata {
    protected String fieldName;
    protected FieldType fieldType;
    protected boolean isInUse;

    public BaseFieldMetadata(String fieldName, FieldType fieldType, boolean isInUse) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isInUse = isInUse;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setInUse(boolean inUse) {
        isInUse = inUse;
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
}
