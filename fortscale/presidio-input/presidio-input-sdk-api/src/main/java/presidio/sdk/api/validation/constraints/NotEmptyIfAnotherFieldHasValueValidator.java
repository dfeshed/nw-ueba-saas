package presidio.sdk.api.validation.constraints;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;
import presidio.sdk.api.validation.NotEmptyIfAnotherFieldHasValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class NotEmptyIfAnotherFieldHasValueValidator implements ConstraintValidator<NotEmptyIfAnotherFieldHasValue, Object> {

    private String fieldName;
    private String[] fieldValues;
    private String dependFieldName;

    @Override
    public void initialize(NotEmptyIfAnotherFieldHasValue notEmptyIfAnotherFieldHasValue) {
        fieldValues = notEmptyIfAnotherFieldHasValue.fieldValues();
        fieldName = notEmptyIfAnotherFieldHasValue.fieldName();
        dependFieldName = notEmptyIfAnotherFieldHasValue.dependFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        Object fieldValue = getFieldValue(value, fieldName);
        Object dependFieldValue = getFieldValue(value, dependFieldName);

        if (ArrayUtils.contains(fieldValues, fieldValue) && (dependFieldValue == null || StringUtils.isEmpty(dependFieldValue.toString()))) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(dependFieldName)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private Object getFieldValue(Object value, String requestedFieldName) {
        Field field = ReflectionUtils.findField(value.getClass(), requestedFieldName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, value);
    }
}
