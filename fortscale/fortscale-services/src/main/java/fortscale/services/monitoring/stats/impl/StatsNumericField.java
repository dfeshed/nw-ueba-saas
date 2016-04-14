package fortscale.services.monitoring.stats.impl;
import fortscale.utils.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(StatsServiceImpl.class);

    // Reflection of the field to read
    Field  field;

    // The object containing the field. Typically it is object derived from StatsMetricsGroup
    Object object;

    /**
     *
     * Read the field value from the object and return it long. Round if the type is float.
     *
     * Calls an internal function to do the actual read.
     *
     * @return value read
     */
    public long getAsLong() {

        try { // Just in case

            long value = internalGetAsLong();

            return value;

        }
        catch (Exception ex) {

            String msg = String.format("Failed to read field %s value as long from %s of type %s",
                                       field.getName(), object.getClass().getName(), field.getType().getName() );
            logger.warn(msg, ex);
            throw ( new StatsMetricsExceptions.StatsEngineFailedToReadFieldValueException(msg, ex));

        }
    }

    /**
     *
     * Read the field value from the object and return it as double
     *
     * Calls an internal function to do the actual read.
     *
     * @return value read
     */
    public double getAsDouble() {

        try { // Just in case

            double value = internalGetAsDouble();

            return value;

        }
        catch (Exception ex) {

            String msg = String.format("Failed to read field %s value as double from %s of type %s",
                    field.getName(), object.getClass().getName(), field.getType().getName() );
            logger.warn(msg, ex);
            throw ( new StatsMetricsExceptions.StatsEngineFailedToReadFieldValueException(msg, ex));

        }
    }


    /**
     *
     * see getAsLong()
     *
     * @return
     * @throws IllegalAccessException
     */
    abstract protected long internalGetAsLong()   throws IllegalAccessException;

    /**
     *
     * see getAsDouble()
     *
     * @return
     * @throws IllegalAccessException
     */
    abstract protected double internalGetAsDouble() throws IllegalAccessException;

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


        // Oops, unsupported data type, exception pls.

        String msg = String.format("Unsupported numeric data type %s of field %s from %s",
                field.getType().getName(), field.getName(), object.getClass().getName() );

        logger.error(msg);

        throw ( new StatsMetricsExceptions.StatsEngineUnsupportedDataTypeException(msg));

    }

}


/**
 *  long & Long field reader
 */
class StatsLongField extends StatsNumericField {

    StatsLongField(Field field, Object object)   {  super(field, object);  }
    public long internalGetAsLong()   throws IllegalAccessException { return (Long)field.get(object);    }
    public double internalGetAsDouble() throws IllegalAccessException { return (double) internalGetAsLong(); }

}

/**
 *  int & Integer field reader
 */
class StatsIntegerField extends StatsNumericField {

    StatsIntegerField(Field field, Object object)   {  super(field, object);  }
    public long internalGetAsLong()   throws IllegalAccessException { return ((Integer)field.get(object)).longValue();    }
    public double internalGetAsDouble() throws IllegalAccessException { return (double) internalGetAsLong(); }

}

/**
 *  double & Double field reader
 */
class StatsDoubleField extends StatsNumericField {

    StatsDoubleField(Field field, Object object)   {  super(field, object);  }
    public long internalGetAsLong()   throws IllegalAccessException { return Math.round(internalGetAsDouble()); }
    public double internalGetAsDouble() throws IllegalAccessException { return (Double)field.get(object); }

}

/**
 *  float & Float field reader
 */
class StatsFloatField extends StatsNumericField {

    StatsFloatField(Field field, Object object)   {  super(field, object);  }
    public long internalGetAsLong()   throws IllegalAccessException { return Math.round(internalGetAsDouble()); }
    public double internalGetAsDouble() throws IllegalAccessException { return ((Float)field.get(object)).doubleValue(); }

}
