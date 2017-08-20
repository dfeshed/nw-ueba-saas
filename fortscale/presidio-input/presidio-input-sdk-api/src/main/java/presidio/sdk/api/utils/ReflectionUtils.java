package presidio.sdk.api.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

    public static Object getFieldValue(Object value, String requestedFieldName) {
        Field field = org.springframework.util.ReflectionUtils.findField(value.getClass(), requestedFieldName);
        field.setAccessible(true);
        return org.springframework.util.ReflectionUtils.getField(field, value);
    }

    public static void setFieldValue(Object value, String requestedFieldName, Object fieldValue) throws IllegalAccessException {
        Field field = org.springframework.util.ReflectionUtils.findField(value.getClass(), requestedFieldName);
        field.setAccessible(true);
        field.set(value, fieldValue);
    }
}
