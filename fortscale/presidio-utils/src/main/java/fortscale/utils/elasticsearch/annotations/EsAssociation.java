package fortscale.utils.elasticsearch.annotations;

import org.springframework.data.annotation.Reference;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface EsAssociation {
}
