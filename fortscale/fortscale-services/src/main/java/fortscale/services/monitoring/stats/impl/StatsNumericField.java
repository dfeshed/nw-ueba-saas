package fortscale.services.monitoring.stats.impl;

import java.lang.reflect.Field;
/**
 * Created by gaashh on 4/4/16.
 */
abstract public class StatsNumericField {

    Field  field;
    Object object;

    abstract public long   getAsLong()   throws IllegalAccessException;
    abstract public double getAsDouble() throws IllegalAccessException;

    protected StatsNumericField(Field field, Object object) {
        this.field  = field;
        this.object = object;
    }

    static StatsNumericField builder(Field field, Object object) {

        Class fieldType = field.getType();

        if (fieldType == long.class || Long.class.isAssignableFrom(fieldType) ) {
            return new StatsLongField(field, object);

        } else if (fieldType == int.class || Integer.class.isAssignableFrom(fieldType) ) {
            return new StatsIntegerField(field, object);

        } else if (fieldType == double.class || Double.class.isAssignableFrom(fieldType) ) {
            return new StatsDoubleField(field, object);

        } else if (fieldType == float.class || Float.class.isAssignableFrom(fieldType) ) {
            return new StatsFloatField(field, object);

        }


        // TODO: throw exception
        return null;

    }



}


class StatsLongField extends StatsNumericField {

    StatsLongField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return (Long)field.get(object);    }
    public double   getAsDouble() throws IllegalAccessException { return (double) getAsLong(); }

}


class StatsIntegerField extends StatsNumericField {

    StatsIntegerField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return ((Integer)field.get(object)).longValue();    }
    public double   getAsDouble() throws IllegalAccessException { return (double) getAsLong(); }

}

class StatsDoubleField extends StatsNumericField {

    StatsDoubleField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return Math.round(getAsDouble()); }
    public double   getAsDouble() throws IllegalAccessException { return (Double)field.get(object); }

}

class StatsFloatField extends StatsNumericField {

    StatsFloatField(Field field, Object object)   {  super(field, object);  }
    public long     getAsLong()   throws IllegalAccessException { return Math.round(getAsDouble()); }
    public double   getAsDouble() throws IllegalAccessException { return ((Float)field.get(object)).doubleValue(); }

}
