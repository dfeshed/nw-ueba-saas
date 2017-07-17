package presidio.sdk.api.validation.constraints;


import fortscale.utils.logging.Logger;
import org.apache.commons.lang.ArrayUtils;
import presidio.sdk.api.validation.AcceptableValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptableValuesValidator implements ConstraintValidator<AcceptableValues, Object> {

    private static final Logger logger = Logger.getLogger(AcceptableValuesValidator.class);

    private String[] fieldValues;

    @Override
    public void initialize(AcceptableValues acceptableValues) {
        fieldValues = acceptableValues.fieldValues();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            logger.info("Validation failed, the object received is null");
            return false;
        }

        if (!ArrayUtils.contains(fieldValues, value)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation();
            logger.info("Validation failed - field should be one of {}, but is {}", fieldValues, value);
            return false;
        }

        logger.debug("Validation passed");
        return true;
    }
}
