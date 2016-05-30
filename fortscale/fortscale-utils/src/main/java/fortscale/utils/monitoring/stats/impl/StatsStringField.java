package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsStringFlexMetric;

import java.lang.reflect.Field;

/**
 *
 * This is an helper class to read string field via reflection.
 *
 * null is supported and preserved. In other words, if the original value was null, the result would be null as well.
 *
 * The static builder() function returns the proper object to read the field.
 *
 * Note: the class hold both the field and the instance containing it.
 *
 * Supported types are: String, StatsStringFlexMetric
 *
 * Created by gaashh on 5/29/16.
 */
abstract public class StatsStringField {

    private static final Logger logger = Logger.getLogger(StatsStringField.class);

    // Reflection of the field to read
    Field  field;

    // The object containing the field. Typically it is object derived from StatsMetricsGroup
    Object object;

    /**
     *
     * Read the field value from the object and return it as String.
     *
     * null is supported and preserved. In other words, if the original value was null, the result would be null as well.
     *
     * Calls an internal function to do the actual read.
     *
     * @return value read
     */
    public String getAsString() {

        try { // Just in case

            String value = internalGetAsString();

            return value;

        }
        catch (Exception ex) {

            String msg = String.format("Failed to read field %s value as String from %s of type %s",
                    field.getName(), object.getClass().getName(), field.getType().getName() );
            logger.warn(msg, ex);
            throw ( new StatsMetricsExceptions.FailedToReadFieldValueException(msg, ex));

        }
    }

    /**
     *
     * see getAsString()
     *
     * @return
     * @throws IllegalAccessException
     */
    abstract protected String internalGetAsString() throws IllegalAccessException;


    /**
     * Saves the field and the object.
     *
     * Objects shall be created only via builder()
     *
     * @param field  - Reflection of the field to read
     * @param object - The object containing the field. Typically it is object derived from StatsMetricsGroup
     */
    protected StatsStringField(Field field, Object object) {
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
    static StatsStringField builder(Field field, Object object) {

        Class fieldType = field.getType();

        // String
        if ( String.class.isAssignableFrom(fieldType) ) {
            return new StatsStringMetricField(field, object);
        }

        // Check for classes that implements flex interfaces
        Class[] interfaces = fieldType.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {

            // StatsStringFlexMetric
            if (interfaces[i] == StatsStringFlexMetric.class) {
                return new StatsFlexStringMetricField(field,object);
            }
        }

        // Oops, unsupported data type, exception pls.

        String msg = String.format("Unsupported String data type %s of field %s from %s",
                field.getType().getName(), field.getName(), object.getClass().getName() );

        logger.error(msg);

        throw ( new StatsMetricsExceptions.UnsupportedDataTypeException(msg));

    }

}


/**
 *  String reader
 */
class StatsStringMetricField extends StatsStringField {

    StatsStringMetricField(Field field, Object object)   {  super(field, object);  }

    public String internalGetAsString()   throws IllegalAccessException {

        Object valueObject = field.get(object);

        if (valueObject == null) {
            return null;
        }

        return (String)valueObject;
    }

}


/**
 *  StatsStringFlexMetric
 */
class StatsFlexStringMetricField extends StatsStringField {

    StatsFlexStringMetricField(Field field, Object object)   {  super(field, object);  }

    public String internalGetAsString()   throws IllegalAccessException {

        // Get the object
        Object valueObject = field.get(object);

        // Check null object
        if (valueObject == null) {
            return null;
        }

        // Get the value by calling the getValue() function
        String value = ((StatsStringFlexMetric)valueObject).getValue();

        return value; // Might be null, that's OK;

    }

}

