package presidio.sdk.api.validation;

import presidio.sdk.api.validation.constraints.FieldsMustHaveDifferentValuesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldsMustHaveDifferentValuesValidator.class)
@Repeatable(value = MultipleFieldsMustHaveDifferentValues.class)
public @interface FieldsMustHaveDifferentValues {

    String message() default "fields must have different values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fieldNames();
    String canBothBeEmpty();

    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        FieldsMustHaveDifferentValues[] value();
    }
}



