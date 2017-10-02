package fortscale.utils.mongodb.index;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When the {@link DynamicIndexingApplicationListener} creates indexes dynamically
 * for a certain collection (derived from a {@link Document}), it uses the options
 * defined in this annotation (if it is added to the {@link Document}).
 *
 * @author Lior Govrin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DynamicIndexing {
	/**
	 * @return an array of compound indexes that should be created dynamically
	 */
	CompoundIndex[] compoundIndexes() default {};

	/**
	 * @return true if the {@link DynamicIndexing} options of the superclass should be inherited, false otherwise
	 */
	boolean inheritFromSuperclass() default true;
}
