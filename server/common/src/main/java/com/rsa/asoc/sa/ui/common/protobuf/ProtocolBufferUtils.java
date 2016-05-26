package com.rsa.asoc.sa.ui.common.protobuf;

import com.google.common.base.Defaults;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Primitives;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol.Array;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol.Dictionary;
import com.rsa.netwitness.carlos.plist.PropertyListProtocol.PropertyList;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Useful utilities for working with Protocol Buffers at a low level.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
public final class ProtocolBufferUtils {

    private ProtocolBufferUtils() {
    }

    /**
     * Converts a {@link Map} to a {@link Dictionary}.  Only primitive wrappers, {@link String}s, {@link Date}s and
     * other dictionaries are supported in the map values.
     */
    public static Dictionary createDictionaryFromMap(Map<String, Object> parameters) {
        Dictionary.Builder dictionaryBuilder = Dictionary.newBuilder();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            PropertyList propertyList = createPropertyList(entry.getValue());
            dictionaryBuilder.addEntry(Dictionary.Entry.newBuilder().setKey(entry.getKey()).setValue(propertyList));
        }
        return dictionaryBuilder.build();
    }

    /**
     * Converts a {@link Dictionary} to a {@link Map}.
     */
    public static Map<String, Object> createMapFromDictionary(Dictionary dictionary) {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        for (Dictionary.Entry entry : dictionary.getEntryList()) {
            map.put(entry.getKey(), getPropertyListValue(entry.getValue()));
        }
        return map;
    }

    /**
     * Creates a {@link Dictionary.Entry} from the given key/value pair.
     */
    public static Dictionary.Entry createEntry(String key, Object value) {
        Dictionary.Entry.Builder entry = Dictionary.Entry.newBuilder();
        entry.setKey(key);

        if (value != null) {
            entry.setValue(createPropertyList(value));
        }

        return entry.build();
    }

    /**
     * Create a {@link PropertyList} from the given value. Only primitive wrappers, {@link String}s, {@link Date}s and
     * dictionaries are supported.  All other values will throw an {@link IllegalArgumentException}.
     */
    public static PropertyList createPropertyList(Object value) {
        checkNotNull(value);
        return createPropertyList(value, value.getClass());
    }

    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public static PropertyList createPropertyList(Object value, Class<?> type) {
        checkNotNull(type);

        // Ensure the type passed in is the primitive wrapper, so we only have to check against
        // one class below.
        type = Primitives.wrap(type);

        if (value == null) {
            value = Defaults.defaultValue(Primitives.unwrap(type));
        }

        PropertyList.Builder builder;
        if (type == String.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.String);

            if (value != null) {
                builder.setString((String) value);
            }
        } else if (type == Double.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Number)
                    .setNumber(PropertyListProtocol.Number.newBuilder()
                            .setType(PropertyListProtocol.Number.Type.REAL_64).setReal64((Double) value));
        } else if (type == Float.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Number)
                    .setNumber(PropertyListProtocol.Number.newBuilder()
                            .setType(PropertyListProtocol.Number.Type.REAL_32).setReal32((Float) value));
        } else if (type == Long.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Number)
                    .setNumber(PropertyListProtocol.Number.newBuilder().setType(PropertyListProtocol.Number.Type.INT_64)
                            .setInt64((Long) value));
        } else if (type == Integer.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Number)
                    .setNumber(PropertyListProtocol.Number.newBuilder().setType(PropertyListProtocol.Number.Type.INT_32)
                            .setInt32((Integer) value));
        } else if (type == Boolean.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Boolean).setBoolean((Boolean) value);
        } else if (type == Date.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Date);

            if (value != null) {
                builder.setDate(((Date) value).getTime());
            }
        } else if (type == Dictionary.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Dictionary);

            if (value != null) {
                builder.setDictionary((Dictionary) value);
            }
        } else if (type == Array.class) {
            builder = PropertyList.newBuilder().setType(PropertyList.Type.Array);

            if (value != null) {
                builder.setArray((Array) value);
            }
        } else if (value instanceof Map) {
            PropertyListProtocol.Dictionary.Builder dictionaryBuilder = PropertyListProtocol.Dictionary.newBuilder();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                if (entry.getValue() != null) {
                    dictionaryBuilder.addEntry(createEntry(entry.getKey(), entry.getValue()));
                }
            }

            builder = PropertyList.newBuilder().setType(PropertyList.Type.Dictionary).setDictionary(dictionaryBuilder);
        } else if (value instanceof Iterable) {
            List<PropertyList> values = Lists.newLinkedList();
            for (Object obj : (Iterable) value) {
                if (obj instanceof PropertyList) {
                    values.add((PropertyList) obj);
                } else {
                    values.add(createPropertyList(obj));
                }
            }

            builder = PropertyList.newBuilder().setType(PropertyList.Type.Array)
                    .setArray(PropertyListProtocol.Array.newBuilder().addAllElement(values));
        } else {
            throw new IllegalArgumentException("Unknown type " + value.getClass().getName());
        }

        return builder.build();
    }

    /**
     * Extracts the Java object from a {@link PropertyList}.
     */
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public static Object getPropertyListValue(PropertyList property) {
        switch (property.getType()) {
            case String:
                return property.getString();
            case Array:
                List<Object> list = Lists.newArrayList();
                for (PropertyListProtocol.PropertyList value : property.getArray().getElementList()) {
                    list.add(getPropertyListValue(value));
                }
                return list;
            case Boolean:
                return property.getBoolean();
            case Data:
                return property.getData();
            case Date:
                if (property.getDate() != 0) {
                    return new Date(property.getDate());
                } else {
                    return null;
                }
            case Dictionary:
                return createMapFromDictionary(property.getDictionary());
            case Number:
                PropertyListProtocol.Number propertyNumeric = property.getNumber();
                switch (propertyNumeric.getType()) {
                    case INT_8:
                        return propertyNumeric.getInt8();
                    case INT_16:
                        return propertyNumeric.getInt16();
                    case INT_32:
                        return propertyNumeric.getInt32();
                    case INT_64:
                        return propertyNumeric.getInt64();
                    case REAL_32:
                        return propertyNumeric.getReal32();
                    case REAL_64:
                        return propertyNumeric.getReal64();
                    default:
                        throw new IllegalArgumentException("Unknown numeric type " + propertyNumeric.getType());
                }
                // fall-through to default
            default:
                throw new IllegalArgumentException("Unknown property type " + property.getType());
        }
    }

    /**
     * Convenience method to hide ugly casts when converting from {@link PropertyList}s to Java objects.
     */
    public static <T> T getPropertyListValue(PropertyList propertyList, Class<T> clazz) {
        Object value = getPropertyListValue(propertyList);
        checkState(clazz.isAssignableFrom(value.getClass()), "Property list value type mismatch");
        return clazz.cast(value);
    }

    /**
     * Retrieve a {@link Dictionary.Entry} from a {@link Dictionary} using the key name.
     */
    public static Dictionary.Entry getDictionaryEntry(Dictionary dictionary, String key) {
        for (Dictionary.Entry entry : dictionary.getEntryList()) {
            if (entry.getKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Retrieve a casted object from a from a {@link Dictionary} using the key name. Returns null if the key
     * is not found.
     */
    public static <T> T getDictionaryValue(Dictionary dictionary, String key, Class<T> type) {
        return getDictionaryValue(dictionary, key, type, null);
    }

    /**
     * Retrieve a casted object from a from a {@link Dictionary} using the key name. Returns null if the key
     * is not found.
     */
    public static <T> T getDictionaryValue(Dictionary dictionary, String key, Class<T> type, T defaultValue) {
        for (Dictionary.Entry entry : dictionary.getEntryList()) {
            if (entry.getKey().equals(key)) {
                return getPropertyListValue(entry.getValue(), type);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a value from a {@link Map}, casts it to the given type, and passes it as a parameter to
     * the {@link Consumer}.  This is convenient when working with maps from the
     * {@link #createMapFromDictionary(Dictionary)} method.
     */
    public static <T> void consumeValueFromMap(Map<String, ?> map, String key, Class<T> type, Consumer<T> consumer) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (!type.isAssignableFrom(value.getClass())) {
                String message = String.format("Cannot cast key '%s' from %s to %s",
                        key,
                        value.getClass().getName(),
                        type.getName());
                throw new IllegalArgumentException(message);
            }
            consumer.accept(type.cast(map.get(key)));
        }
    }

    /**
     * Converts the value, which must be a {@link String} in the map to a {@link Enum} and passes it onto the consumer
     */
    public static <T extends Enum<T>> void consumeEnumFromMap(Map<String, ?> map, String key, Class<T> type,
            Consumer<T> consumer) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (!(value instanceof String)) {
                throw new IllegalArgumentException(String
                        .format("Cannot convert to enum using type %s, must be a String", value.getClass()));
            }
            String string = (String) value;

            consumer.accept(Enum.valueOf(type, string));
        }
    }

    /**
     * Converts the value, which must be a {@link Date}, in the map to an {@link Instant} and passes it onto the
     * consumer
     */
    public static <T extends Instant> void consumeInstantFromMap(Map<String, ?> map, String key, Consumer<T> consumer) {
        if (map.containsKey(key)) {
            Object value = map.get(key);
            if (!(value instanceof Date)) {
                throw new IllegalArgumentException(String
                        .format("Cannot convert to Instant using type %s, must be a Date", value.getClass()));
            }
            Date date = (Date) value;

            consumer.accept((T) (date.toInstant()));
        }
    }
}
