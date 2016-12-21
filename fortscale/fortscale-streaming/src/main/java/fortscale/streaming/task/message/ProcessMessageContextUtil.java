package fortscale.streaming.task.message;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * helper class that should be used when initiating {@link ProcessMessageContext}
 * Created by baraks on 12/19/2016.
 */
public class ProcessMessageContextUtil {

    public static JSONObject parseJsonMessage(String msg, AtomicLong parseToJsonCounter,AtomicLong parseToJsonExceptionCounter) throws ParseException {
        JSONObject jsonObject = null;
        try {
            if(parseToJsonCounter!=null) {
                parseToJsonCounter.incrementAndGet();
            }
            jsonObject = parseJsonMessage(msg);
        } catch (Exception e) {
            if(parseToJsonExceptionCounter!=null) {
                parseToJsonExceptionCounter.incrementAndGet();
            }
        }
        return jsonObject;
    }

    public static JSONObject parseJsonMessage(String msg) throws Exception {
        return (JSONObject) JSONValue.parseWithException(msg);
    }


}
