package fortscale.utils.reflection;

import org.apache.commons.lang3.tuple.Pair;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.Validate.notNull;

public class ReflectionUtils {
    private static final String NULL_ROOT_EXCEPTION_MESSAGE = "root cannot be null.";
    private static final String NULL_KEY_EXCEPTION_MESSAGE = "key cannot be null.";
    private static final String DELIMITING_REGEX = "\\.";

    public static <T> T get(Object root, String key) {
        Pair<Object, String> leafParentAndName = getLeafParentAndName(root, key);
        return flatGet(leafParentAndName.getLeft(), leafParentAndName.getRight());
    }

    public static <T> void set(Object root, String key, T value) {
        Pair<Object, String> leafParentAndName = getLeafParentAndName(root, key);
        flatSet(leafParentAndName.getLeft(), leafParentAndName.getRight(), value);
    }

    public static <T> T get(Object root, String key, T defaultValue) {
        Object object = notNull(root, NULL_ROOT_EXCEPTION_MESSAGE);
        String[] subkeys = notNull(key, NULL_KEY_EXCEPTION_MESSAGE).split(DELIMITING_REGEX);
        int lastIndex = subkeys.length - 1;

        for (int i = 0; i < lastIndex; ++i) {
            object = flatGet(object, subkeys[i]);
            if (object == null) return defaultValue;
        }

        return flatGet(object, subkeys[lastIndex]);
    }

    public static List<Field> findHierarchyFields(Class<?> clazz, String key) {
        List<Field> hierarchyFields = new ArrayList<>();

        for (String name : key.split(DELIMITING_REGEX)) {
            Field field = findField(clazz, name);
            hierarchyFields.add(field);
            clazz = field.getType();
        }

        return hierarchyFields;
    }

    public static Set<Class<?>> getSubTypesOf(List<String> prefixes, Class<?> type) {
        return prefixes.stream()
                .map(Reflections::new)
                .map(reflections -> reflections.getSubTypesOf(type))
                .flatMap(Set::stream)
                .collect(toSet());
    }

    /**
     * Get the type of the property of the given {@link Class}, that is represented by the given key. The key can be
     * hierarchical, meaning that all the properties in the hierarchy are traversed, from the {@link Class} (i.e. the
     * root) to the last property (i.e. the leaf), and the type of the last property is returned. If the key does not
     * represent a valid property of the {@link Class} (or a valid hierarchy of properties), an exception is thrown.
     *
     * @param clazz The {@link Class}.
     * @param key   The key.
     * @return The type of the property.
     */
    public static Class<?> getPropertyType(Class<?> clazz, String key) {
        List<Field> hierarchyFields = findHierarchyFields(clazz, key);
        return hierarchyFields.get(hierarchyFields.size() - 1).getType();
    }

    private static Pair<Object, String> getLeafParentAndName(Object root, String key) {
        Object object = notNull(root, NULL_ROOT_EXCEPTION_MESSAGE);
        String[] subkeys = notNull(key, NULL_KEY_EXCEPTION_MESSAGE).split(DELIMITING_REGEX);
        int lastIndex = subkeys.length - 1;

        for (int i = 0; i < lastIndex; ++i) {
            object = flatGet(object, subkeys[i]);
            if (object == null) throw new BrokenHierarchyException(root, key, subkeys[i], i);
        }

        return Pair.of(object, subkeys[lastIndex]);
    }

    private static <T> T flatGet(Object object, String name) {
        try {
            // noinspection unchecked - Class cast exception might be thrown.
            return (T)findField(object.getClass(), name).get(object);
        } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
    }

    private static <T> void flatSet(Object object, String name, T value) {
        try {
            findField(object.getClass(), name).set(object, value);
        } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException(illegalAccessException);
        }
    }

    private static Field findField(Class<?> clazz, String name) {
        Field field = org.springframework.util.ReflectionUtils.findField(clazz, name);
        notNull(field, "Class %s does not have a field named %s.", clazz.getName(), name);
        field.setAccessible(true);
        return field;
    }

    private static final class BrokenHierarchyException extends RuntimeException {
        public BrokenHierarchyException(Object root, String key, String subkey, int i) {
            super(format("Broken hierarchy - root: %s, key: %s, subkey: %s, i: %d.", root.toString(), key, subkey, i));
        }
    }
}
