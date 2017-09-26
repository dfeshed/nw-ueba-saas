package fortscale.utils.mongodb.index;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When the {@link DynamicIndexApplicationListener} creates indexes dynamically
 * for a certain collection (derived from a {@link Document}), it uses the options
 * defined in this annotation (if it is added to the {@link Document}).
 *
 * @author Lior Govrin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicIndexing {
	/**
	 * @return true if the {@link CompoundIndex} definitions of the {@link Document}'s
	 *         properties (fields) should be included, false otherwise.
	 */
	boolean includeCompoundIndexDefinitionsOfProperties() default true;
}
