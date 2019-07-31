package presidio.sdk.api.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class ReflectionUtils {

    public static Object getFieldValue(Object value, String requestedFieldName) {
        Field field = getAccessibleField(value, requestedFieldName);
        return org.springframework.util.ReflectionUtils.getField(field, value);
    }

    public static void setFieldValue(Object value, String requestedFieldName, Object fieldValue) throws IllegalAccessException {
        if (requestedFieldName.contains(".")) {
            String[] nestedObjects = requestedFieldName.split("\\.");
            Field field = getAccessibleField(value, nestedObjects[0]);
            setFieldValue(field.get(value), requestedFieldName.substring(requestedFieldName.indexOf(".") + 1), fieldValue);
        } else {
            Field field = getAccessibleField(value, requestedFieldName);
            field.set(value, fieldValue);
        }
    }

    private static Field getAccessibleField(Object object, String fieldName) {
        Field field = org.springframework.util.ReflectionUtils.findField(object.getClass(), fieldName);
        Objects.requireNonNull(field).setAccessible(true);
        return field;
    }
}
