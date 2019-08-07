package presidio.sdk.api.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectionUtils {

    private static final String NESTED_OBJECT_DELIMITER = ".";
    private static final String NESTED_OBJECT_SPLIT_DELIMITER = "\\.";

    public static Object getFieldValue(Object obj, String requestedFieldName) {
        Field field = getAccessibleField(obj.getClass(), requestedFieldName);
        return org.springframework.util.ReflectionUtils.getField(field, obj);
    }

    public static void setFieldValue(Object obj, String requestedFieldName, Object fieldValue) throws IllegalAccessException {
        NestedObject nestedObjectAndField = findNestedObject(obj.getClass(), obj, requestedFieldName, new StringBuilder());
        nestedObjectAndField.field.set(nestedObjectAndField.object, fieldValue);
    }

    public static String findFieldNameRecursively(Class clazz, String requestedFieldName) {
        return findNestedObject(clazz, null, requestedFieldName, new StringBuilder()).fieldName;
    }

    private static NestedObject findNestedObject(Class clazz, Object obj, String requestedFieldName, StringBuilder stringBuilder) {
        if (requestedFieldName.contains(NESTED_OBJECT_DELIMITER)) {
            String[] nestedObjects = requestedFieldName.split(NESTED_OBJECT_SPLIT_DELIMITER);
            Field field = getAccessibleField(clazz, nestedObjects[0]);
            Object nestedObject = obj;
            if (obj != null) {
                try {
                    nestedObject = field.get(obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            String fieldName = getCurrentFieldName(field);
            stringBuilder.append(fieldName).append(NESTED_OBJECT_DELIMITER);
            return findNestedObject(field.getType(), nestedObject,
                    requestedFieldName.substring(requestedFieldName.indexOf(NESTED_OBJECT_DELIMITER) + 1), stringBuilder);
        } else {
            Field field = getAccessibleField(clazz, requestedFieldName);
            String fieldName = stringBuilder.toString() + getCurrentFieldName(field);
            if (fieldName.endsWith(NESTED_OBJECT_DELIMITER)) {
                fieldName = fieldName.substring(0, fieldName.length() - 1);
            }
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
