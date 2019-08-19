package fortscale.utils.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PresidioReflectionUtils {

    private static final String NESTED_OBJECT_DELIMITER = ".";
    private static final String NESTED_OBJECT_SPLIT_DELIMITER = "\\.";

    public static Object getFieldValue(Object obj, String requestedFieldName) {
        Field field = getAccessibleField(obj.getClass(), requestedFieldName);
        return org.springframework.util.ReflectionUtils.getField(field, obj);
    }

    /**
     * Sets the given object's field to the given value. This method works for nested objects also.
     * If one would like to change a nested object's field, the nested object delimiter should be found between
     * objects in the field name.
     * For example:
     * setFieldValue(obj = {
     *     "name": "Aaron",
     *     "cat": {
     *         "toy": "mouse"
     *     }
     * }, fieldName = "cat.toy", fieldValue = "rabbit")
     * becomes
     * {
     *    "name": "Aaron",
     *     "cat": {
     *       "toy": "rabbit"
*           }
     * }
     *
     * @param obj the given object
     * @param fieldName the field name belonging to the object which should be set to a the given value
     * @param fieldValue the value to set the object's field to
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException {
        List<Field> fields = findNestedFields(obj.getClass(), fieldName);
        for (int i = 0; i < fields.size() - 1; i++) {
            obj = fields.get(i).get(obj);
        }
        fields.get(fields.size() - 1).set(obj, fieldValue);
    }

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
    public static String findFieldNameRecursively(Class clazz, String requestedFieldName) {
        return findNestedFields(clazz, requestedFieldName)
                .stream()
                .map(PresidioReflectionUtils::getCurrentFieldName)
                .collect(Collectors.joining(NESTED_OBJECT_DELIMITER));
    }

    private static List<Field> findNestedFields(Class clazz, String requestedFieldName) {
        ArrayList<Field> fields = new ArrayList<>();
        for (String fieldName : requestedFieldName.split(NESTED_OBJECT_SPLIT_DELIMITER)) {
            Field field = getAccessibleField(clazz, fieldName);
            fields.add(field);
            clazz = field.getType();
        }
        return fields;
    }

   /* private static String getCurrentFieldName(Field field) {
        return field.getName();
    }
*/
    private static String getCurrentFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
            fieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
        }
        return fieldName;
    }

    private static Field getAccessibleField(Class clazz, String fieldName) {
        Field field = org.springframework.util.ReflectionUtils.findField(clazz, fieldName);
        Objects.requireNonNull(field).setAccessible(true);
        return field;
    }
}
