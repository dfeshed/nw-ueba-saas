package presidio.sdk.api.validation;

import presidio.sdk.api.validation.constraints.NotEmptyIfAnotherFieldHasValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyIfAnotherFieldHasValueValidator.class)
@Repeatable(value = MultipleNotEmptyIfAnotherFieldHasValue.class)
public @interface NotEmptyIfAnotherFieldHasValue {

    String message() default "field must exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName();
    String[] fieldValues();
    String dependFieldName();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        NotEmptyIfAnotherFieldHasValue[] value();
    }
}



