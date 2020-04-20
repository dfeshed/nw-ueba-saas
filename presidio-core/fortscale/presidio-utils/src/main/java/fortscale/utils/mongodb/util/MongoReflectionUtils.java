package fortscale.utils.mongodb.util;

import fortscale.utils.reflection.ReflectionUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.stream.Collectors;

public class MongoReflectionUtils {
    private static final String HIERARCHY_FIELDS_DELIMITER = ".";

    /**
     * Finds the field name recursively.
     * If an annotation exists returns the field name of the annotation, otherwise returns the original field name.
     * Works for nested classes also and concatenates the field names with nested object delimiter.
     * For example:
     * findFieldNameRecursively(clazz = {
     *     @Field("coolName")
     *     private Object obj = {
     *         private Object city = {
     *             @Field("coolStreet")
     *             private String street;
     *         }
     *     }
     * }, "obj.city.street")
     * will return "coolName.city.coolStreet"
     * @param clazz the class on which to find the field name
     * @param requestedFieldName the field path to look for
     */
    public static String findFieldNameRecursively(Class<?> clazz, String requestedFieldName) {
        return ReflectionUtils.findHierarchyFields(clazz, requestedFieldName).stream()
                .map(field -> {
                    if (field.isAnnotationPresent(Field.class)) {
                        return field.getAnnotation(Field.class).value();
                    } else {
                        return field.getName();
                    }
                })
                .collect(Collectors.joining(HIERARCHY_FIELDS_DELIMITER));
    }
}
