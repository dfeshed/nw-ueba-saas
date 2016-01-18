package fortscale.services.configuration.gds.state.field;

/**
 * Field metadata representation
 *
 * @author gils
 * 17/01/2016
 */
public class FieldMetadata extends BaseFieldMetadata{

    public FieldMetadata(String fieldName, FieldType fieldType, boolean isAdditionalField) {
        super(fieldName, fieldType, true, isAdditionalField);
    }

    public FieldMetadata(String fieldName, FieldType fieldType, boolean isInUse, boolean isAdditionalField) {
        super(fieldName, fieldType, isInUse, isAdditionalField);
    }

    @Override
    public String toString() {
        return "FieldMetadata: " + super.toString();
    }
}
