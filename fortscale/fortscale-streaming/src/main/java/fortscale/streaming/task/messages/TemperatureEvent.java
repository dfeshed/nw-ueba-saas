package fortscale.streaming.task.messages;

/**
 * Immutable Temperature Event class. The process control system creates these events. The
 * TemperatureEventHandler picks these up and processes them.
 */
public class TemperatureEvent {

    /** Temperature in Celcius. */
    private int temperature;
    
    /** Time temerature reading was taken. */
    private Long timeOfReading;

    private int eventId;


    /**
     * Single value constructor.
     * @param value Temperature in Celsius.
     */
    /**
     * Temeratur constructor.
     * @param temperature Temperature in Celsius
     * @param timeOfReading Time of Reading
     */
    public TemperatureEvent(int temperature, Long timeOfReading, int eventId) {
        this.temperature = temperature;
        this.timeOfReading = timeOfReading;
        this.eventId = eventId;
    }

    /**
     * Get the Temperature.
     * @return Temperature in Celsius
     */
    public int getTemperature() {
        return temperature;
    }
       
    /**
     * Get time Temperature reading was taken.
     * @return Time of Reading
     */
    public Long getTimeOfReading() {
        return timeOfReading;
    }

    public int getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return "TemperatureEvent [" + temperature + "C]";
    }

}
