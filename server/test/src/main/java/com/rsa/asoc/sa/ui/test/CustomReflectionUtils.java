package com.rsa.asoc.sa.ui.test;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author Jay Garala
 * @since 10.6.0
 */
public class CustomReflectionUtils {

    /**
     * Allows setting a field on an object that is (private) static final
     *
     * @param clazz Class containing the static final field
     * @param fieldName The static final field name
     * @param instance Instance of the class contain the static final field
     * @param object Object to set on the static final field
     * @throws NoSuchFieldException Field name does not exist in the class
     * @throws IllegalAccessException Could not set the object onto the static final field
     */
    public static void setStaticFinalObject(Class clazz, String fieldName, Object instance, Object object)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = ReflectionUtils.findField(clazz, fieldName);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.setAccessible(true);

        field.set(instance, object);
    }
}
