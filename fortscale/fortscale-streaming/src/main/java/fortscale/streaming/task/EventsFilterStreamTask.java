package fortscale.streaming.task;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;


public class EventsFilterStreamTask extends AbstractStreamTask{

//	private static final Logger logger = LoggerFactory.getLogger(EventsFilterStreamTask.class);
	
	
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// parse the message into json 
		String messageText = (String)envelope.getMessage();
		JSONObject message = (JSONObject) JSONValue.parseWithException(messageText);
		
		
		
		
		if (!acceptMessage(message)) {
			return;
		}
		
		
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {

	}
	
	/** Auxiliary method to enable filtering messages on specific events types */
	protected boolean acceptMessage(JSONObject message){ return true;}
}
