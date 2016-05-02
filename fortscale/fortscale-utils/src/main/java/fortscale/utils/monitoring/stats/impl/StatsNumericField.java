package fortscale.utils.monitoring.stats.impl;
import fortscale.utils.monitoring.stats.StatsDoubleFlexMetric;
import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * This is an helper class to read numeric field via reflection. It can read the field both as Long and as Double.
 *
 * null is supported and preserved. In other words, if the original value was null, the result would be null as well.
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
     * null is supported and preserved. In other words, if the original value was null, the result would be null as well.
     *
     * Calls an internal function to do the actual read.
     *
     * @return value read
     */
    public Long getAsLong() {

        try { // Just in case

            Long value = internalGetAsLong();

            return value;

        }
        catch (Exception ex) {

            String msg = String.format("Failed to read field %s value as long from %s of type %s",
                                       field.getName(), object.getClass().getName(), field.getType().getName() );
            logger.warn(msg, ex);
            throw ( new StatsMetricsExceptions.FailedToReadFieldValueException(msg, ex));

        }
    }

    /**
     *
     * Read the field value from the object and return it as double
     *
     * null is supported and preserved. In other words, if the original value was null, the result would be null as well.
     *
     * Calls an internal function to do the actual read.
     *
     * @return value read
     */
    public Double getAsDouble() {

        try { // Just in case

            Double value = internalGetAsDouble();

            return value;

        }
        catch (Exception ex) {

            String msg = String.format("Failed to read field %s value as double from %s of type %s",
                    field.getName(), object.getClass().getName(), field.getType().getName() );
            logger.warn(msg, ex);
            throw ( new StatsMetricsExceptions.FailedToReadFieldValueException(msg, ex));

        }
    }


    /**
     *
     * see getAsLong()
     *
     * @return
     * @throws IllegalAccessException
     */
    abstract protected Long internalGetAsLong()   throws IllegalAccessException;

    /**
     *
     * see getAsDouble()
     *
     * @return
     * @throws IllegalAccessException
     */
    abstract protected Double internalGetAsDouble() throws IllegalAccessException;


    /**
     *
     * An helper function to convert Long to Double. If value is null, result is null as well
     *
     * @param value
     * @return
     */
    static protected Double convertLongToDouble(Long value) {

        // Check null value
        if (value == null) {
            return null;
        }

        // Convert
        return (double) value;
    }

    /**
     *
     * An helper function to convert Double to Long. If value is null, result is null as well.
     *
     * Convertion is round up.
     *
     * @param value
     * @return
     */
    static protected Long convertDoubleToLong(Double value) {

        // Check null value
        if (value == null) {
            return null;
        }

        // Convert (round)
        return Math.round(value);

    }




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

        // AtomicLong
        } else if (fieldType == AtomicLong.class) {
            return new StatsAtomicLongField(field, object);

        // AtomicInteger
        } else if (fieldType == AtomicInteger.class) {
            return new StatsAtomicIntegerField(field, object);

        }

        // Check for classes that implements flex interfaces
        Class[] interfaces = fieldType.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {

            // StatsLongFlexMetric
            if (interfaces[i] == StatsLongFlexMetric.class) {
                return new StatsFlexLongMetricField(field,object);

            // StatsDoubleFlexMetric
            } else if (interfaces[i] == StatsDoubleFlexMetric.class) {
                return new StatsFlexDoubleMetricField(field,object);
            }
        }
        
        // Oops, unsupported data type, exception pls.

        String msg = String.format("Unsupported numeric data type %s of field %s from %s",
                field.getType().getName(), field.getName(), object.getClass().getName() );

        logger.error(msg);

        throw ( new StatsMetricsExceptions.UnsupportedDataTypeException(msg));

    }

}


/**
 *  long & Long field reader
 */
class StatsLongField extends StatsNumericField {

    StatsLongField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        return (Long)valueObject;
    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        return convertLongToDouble( internalGetAsLong() );

    }

}

/**
 *  int & Integer field reader
 */
class StatsIntegerField extends StatsNumericField {

    StatsIntegerField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        return ((Integer)valueObject).longValue();
    }

    public Double internalGetAsDouble() throws IllegalAccessException {
        
        return convertLongToDouble( internalGetAsLong() );
    
    }

}

/**
 *  double & Double field reader
 */
class StatsDoubleField extends StatsNumericField {

    StatsDoubleField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        return convertDoubleToLong( internalGetAsDouble() );
    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        return (Double)valueObject;
    }

}

/**
 *  float & Float field reader
 */
class StatsFloatField extends StatsNumericField {

    StatsFloatField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        return convertDoubleToLong( internalGetAsDouble() );
    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        return ((Float)valueObject).doubleValue();
    }

}

/**
 *  StatsLongFlexMetric
 */
class StatsFlexLongMetricField extends StatsNumericField {

    StatsFlexLongMetricField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        // Get the object
        Object valueObject = field.get(object);

        // Check null object
        if (valueObject == null) {
            return null;
        }

        // Get the value by calling the getValue() function
        Long value = ((StatsLongFlexMetric)valueObject).getValue();
        
        return value; // Might be null, that's OK;

    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        return convertLongToDouble( internalGetAsLong() );
        
    }

}

/**
 *  StatsDoubleFlexMetric
 */
class StatsFlexDoubleMetricField extends StatsNumericField {

    StatsFlexDoubleMetricField(Field field, Object object)   {  super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        return convertDoubleToLong(internalGetAsDouble());
    
    }    

    public Double internalGetAsDouble() throws IllegalAccessException {


        // Get the object
        Object valueObject = field.get(object);

        // Check null object
        if (valueObject == null) {
            return null;
        }

        // Get the value by calling the getValue() function
        Double value = ((StatsDoubleFlexMetric)valueObject).getValue();

        return value; // Might be null, that's OK;

    }

}

/**
 *  AtomicLong field reader
 */
class StatsAtomicLongField extends StatsNumericField {

    StatsAtomicLongField(Field field, Object object)   { super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        long value = ((AtomicLong)valueObject).longValue();

        return value;
    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        return convertLongToDouble(internalGetAsLong());

    }

}

/**
 *  AtomicInteger field reader
 */
class StatsAtomicIntegerField extends StatsNumericField {

    StatsAtomicIntegerField(Field field, Object object)   { super(field, object);  }

    public Long internalGetAsLong()   throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        long value = ((AtomicInteger)valueObject).longValue();

        return value;
    }

    public Double internalGetAsDouble() throws IllegalAccessException {

        return convertLongToDouble(internalGetAsLong());

    }

}
