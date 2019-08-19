package fortscale.utils.mongodb.util;

import fortscale.utils.reflection.PresidioReflectionUtils;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class MongoReflectionUtils {

    private static final String NESTED_OBJECT_DELIMITER = ".";
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
    public String findFieldNameRecursively(Class clazz, String requestedFieldName) {
        return PresidioReflectionUtils.findNestedFields(clazz, requestedFieldName)
                .stream()
                .map(this::getConfiguredFieldName)
                .collect(Collectors.joining(NESTED_OBJECT_DELIMITER));
    }

    private String getConfiguredFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
            fieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
        }
        return fieldName;
    }
}
