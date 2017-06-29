package presidio.sdk.api.validation.constraints;


import org.apache.commons.lang.ArrayUtils;
import presidio.sdk.api.validation.AcceptableValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptableValuesValidator implements ConstraintValidator<AcceptableValues, Object> {

    private String[] fieldValues;

    @Override
    public void initialize(AcceptableValues acceptableValues) {
        fieldValues = acceptableValues.fieldValues();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        if (!ArrayUtils.contains(fieldValues, value)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
