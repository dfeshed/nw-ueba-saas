package fortscale.utils.reflection;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectionUtils {

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
        NestedObject nestedObjectAndField = findNestedObject(obj.getClass(), obj, fieldName, new StringBuilder());
        nestedObjectAndField.field.set(nestedObjectAndField.object, fieldValue);
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
        try {
            return findNestedObject(clazz, null, requestedFieldName, new StringBuilder()).fieldName;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(String.format("Exception while getting the field name of %s from %s.", requestedFieldName, clazz), e);
        }
    }

    private static NestedObject findNestedObject(Class clazz, Object obj, String requestedFieldName, StringBuilder stringBuilder) throws IllegalAccessException {
        if (requestedFieldName.contains(NESTED_OBJECT_DELIMITER)) {
            String[] nestedObjects = requestedFieldName.split(NESTED_OBJECT_SPLIT_DELIMITER);
            Field field = getAccessibleField(clazz, nestedObjects[0]);
            Object nestedObject = obj;
            if (obj != null) {
                nestedObject = field.get(obj);
            }
            String fieldName = getCurrentFieldName(field);
            stringBuilder.append(fieldName).append(NESTED_OBJECT_DELIMITER);
            return findNestedObject(field.getType(), nestedObject,
                    requestedFieldName.substring(requestedFieldName.indexOf(NESTED_OBJECT_DELIMITER) + 1), stringBuilder);
        } else {
            Field field = getAccessibleField(clazz, requestedFieldName);
            String fieldName = stringBuilder.toString() + getCurrentFieldName(field);
            return new NestedObject(obj, field, fieldName);
        }
    }

    private static String getCurrentFieldName(Field field) {
        String fieldName = field.getName();
        if (field.isAnnotationPresent(org.springframework.data.mongodb.core.mapping.Field.class)) {
            fieldName = field.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class).value();
        }
        return fieldName;
    }

    private static class NestedObject {
        private Object object;
        private Field field;
        private String fieldName;

        NestedObject(Object object, Field field, String fieldName) {
            this.object = object;
            this.field = field;
            this.fieldName = fieldName;
        }
    }

    private static Field getAccessibleField(Class clazz, String fieldName) {
        Field field = org.springframework.util.ReflectionUtils.findField(clazz, fieldName);
        Objects.requireNonNull(field).setAccessible(true);
        return field;
    }
}
