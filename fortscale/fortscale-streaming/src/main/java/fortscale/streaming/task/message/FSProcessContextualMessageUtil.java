package fortscale.streaming.task.message;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * helper class that should be used when initiating {@link FSProcessContextualMessage}
 * Created by baraks on 12/19/2016.
 */
public class FSProcessContextualMessageUtil {

    public static JSONObject parseJsonMessage(String msg, AtomicLong parseToJsonCounter,AtomicLong parseToJsonExceptionCounter) throws ParseException {
        try {
            if(parseToJsonCounter!=null) {
                parseToJsonCounter.incrementAndGet();
            }
            return parseJsonMessage(msg);
        } catch (ParseException e) {
            if(parseToJsonExceptionCounter!=null) {
                parseToJsonExceptionCounter.incrementAndGet();
            }
            throw e;
        }
    }

    public static JSONObject parseJsonMessage(String msg) throws ParseException {
        return (JSONObject) JSONValue.parseWithException(msg);
    }


}
