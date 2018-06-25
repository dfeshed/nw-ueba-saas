package fortscale.utils.elasticsearch.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Jarvis Song
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface JoinColumn {

    String name() default "";

    String referencedColumnName() default "";

}