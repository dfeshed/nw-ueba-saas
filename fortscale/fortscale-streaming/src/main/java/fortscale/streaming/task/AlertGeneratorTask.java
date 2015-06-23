package fortscale.streaming.task;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import fortscale.streaming.task.messages.TemperatureEvent;
import fortscale.streaming.task.messages.TimestampEnd;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import java.util.Date;
import java.util.Random;

/**
 * Created by danal on 16/06/2015.
 */
public class AlertGeneratorTask extends AbstractStreamTask{

	private EPServiceProvider epService;

	@Override protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {
	}

	Long prevStartTime = 0l;
	private int eventId = 0;
	boolean startWasSend = false ;
	boolean endWasSend = true;
	int iteration = 0;
	int sessionId = 0;

	@Override protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		epService.getEPRuntime().sendEvent(new TemperatureEvent(new Random().nextInt(500), new Date().getTime()+new Random().nextInt(100000),++eventId));
		iteration++;
		if(iteration%300 == 0) {
			if (endWasSend) {
				boolean sendStart = new Random().nextBoolean();
				if (sendStart) {
					prevStartTime = new Date().getTime();
					startWasSend = true;
					endWasSend = false;
					MonitorEventSubscriber3 monitorEventSubscriber3 = new MonitorEventSubscriber3(epService,prevStartTime, sessionId);
					System.out.println("started session " + sessionId + " on" + new Date(prevStartTime));
				}
			}
		}
		if(iteration%700 == 0) {
			if (startWasSend) {
				boolean sendEnd = new Random().nextBoolean();
				if (sendEnd) {
					Long endTime = prevStartTime + new Random().nextInt(300000);
					epService.getEPRuntime().sendEvent(new TimestampEnd(endTime, sessionId));
					endWasSend = true;
					startWasSend = false;
					System.out.println("end session " + sessionId + "  on" + new Date(endTime));
					sessionId++;
				}

			}
		}
	}

	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception {
		Configuration esperConfig = new Configuration();
		esperConfig.addEventTypeAutoName("fortscale.streaming.task.messages");
		epService = EPServiceProviderManager.getDefaultProvider(esperConfig);
		epService.getEPAdministrator().createEPL("insert into OrderTemperatureEvent select * from TemperatureEvent.win:time_batch(30 sec) order by timeOfReading");


		MonitorEventSubscriber monitorEventSubscriber = new MonitorEventSubscriber(epService);
		MonitorEventSubscriber2 monitorEventSubscriber2 = new MonitorEventSubscriber2(epService);
		//example of pattern currently not active
		//MonitorEventSubscriber4 monitorEventSubscriber4 = new MonitorEventSubscriber4(epService);

	}

	@Override protected void wrappedClose() throws Exception {

	}
}
