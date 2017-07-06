package presidio.sdk.api.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleNotEmptyIfAnotherFieldHasValue {
    NotEmptyIfAnotherFieldHasValue[] value();
}