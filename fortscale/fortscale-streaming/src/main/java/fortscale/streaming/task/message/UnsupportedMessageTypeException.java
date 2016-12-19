package fortscale.streaming.task.message;

/**
 * Created by baraks on 12/19/2016.
 */
public class UnsupportedMessageTypeException extends RuntimeException {
    public UnsupportedMessageTypeException(Object message) {
        super(String.format("message of type: %s is not supported",message.getClass().getName()));
    }
}
