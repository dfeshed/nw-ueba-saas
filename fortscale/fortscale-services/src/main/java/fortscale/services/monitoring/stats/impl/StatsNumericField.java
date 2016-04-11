package fortscale.services.monitoring.stats.impl;

import java.lang.reflect.Field;
/**
 *
 * This is an helper class to read numeric field via reflection. It can read the field both a long and as double.
 *
 * The static builder() function returns the proper object to read the field.
 *
 * Note: the class hold both the field and the instance containing it.
 *
 * Supported types are:
 *   - long, Long, int, integer
 *   - double, Double, float, Float
 *
 * Created by gaashh on 4/4/16.
 */
abstract public class StatsNumericField {

    // Reflection of the field to read
    Field  field;

    // The object containing the field. Typically it is object derived from StatsMetricsGroup
    Object object;

    // Reads the object value and returns its value as long. Round if needed
    abstract public long   getAsLong()   throws IllegalAccessException;

    // Reads the object value and returns its value as double.
    abstract public double getAsDouble() throws IllegalAccessException;

    /**
     * Saves the field and the object.
     *
     * Objects shall be created only via builder()
     *
     * @param field - Reflection of the field to read
     * @param object -  The object containing the field. TTypically it is object derived from StatsMetricsGroup
     */
    protected StatsNumericField(Field field, Object object) {
        this.field  = field;
        this.object = object;
    }


    /**
     *
     * Returns a new instance of a class that can read the field. The object is created per the field type
     *
     * @param field
     * @param object
     * @return
     */
    static StatsNumericField builder(Field field, Object object) {

        Class fieldType = field.getType();

        // long & Long
        if (fieldType == long.class || Long.class.isAssignableFrom(fieldType) ) {
            return new StatsLongField(field, object);

        // int & Integer
        } else if (fieldType == int.class || Integer.class.isAssignableFrom(fieldType) ) {
            return new StatsIntegerField(field, object);

        // double & Double
        } else if (fieldType == double.class || Double.class.isAssignableFrom(fieldType) ) {
            return new StatsDoubleField(field, object);

        // float & Float
        } else if (fieldType == float.class || Float.class.isAssignableFrom(fieldType) ) {
            return new StatsFloatField(field, object);

        }


        // TODO: throw exception
        return null;

    }



}


/**
 *  long & Long field reader
 */
class StatsLongField extends StatsNumericField {

    StatsLongField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return (Long)field.get(object);    }
    public double   getAsDouble() throws IllegalAccessException { return (double) getAsLong(); }

}

/**
 *  int & Integer field reader
 */
class StatsIntegerField extends StatsNumericField {

    StatsIntegerField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return ((Integer)field.get(object)).longValue();    }
    public double   getAsDouble() throws IllegalAccessException { return (double) getAsLong(); }

}

/**
 *  double & Double field reader
 */
class StatsDoubleField extends StatsNumericField {

    StatsDoubleField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return Math.round(getAsDouble()); }
    public double   getAsDouble() throws IllegalAccessException { return (Double)field.get(object); }

}

/**
 *  float & Float field reader
 */
class StatsFloatField extends StatsNumericField {

    StatsFloatField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return Math.round(getAsDouble()); }
    public double   getAsDouble() throws IllegalAccessException { return ((Float)field.get(object)).doubleValue(); }

}
