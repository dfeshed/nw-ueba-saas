package fortscale.flume;

import com.google.common.base.Charsets;
import junit.framework.TestCase;
import org.apache.flume.*;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.LoggerFactory;

/**
 * Created by tomerd on 09/07/2015.
 */
public  class FsSinkTest extends TestCase {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FsSinkTest.class);
	private static final String hostname = "127.0.0.1";
	private static final Integer port = 4444;

	private static FsSink sink;
	private static Channel channel;

	@BeforeClass public static void setUpClass() {

		// Sink to test
		sink = new FsSink();

		// Channel for testing
		channel = new MemoryChannel();

		Context context = new Context();

		context.put("hostname", hostname);
		context.put("syslogport", String.valueOf(port));
		context.put("batchSize", String.valueOf(1));
		context.put("messagemaxlength", String.valueOf(2048));
		context.put("transportProtocol", "TCP");
		context.put("useCompression", "true");

		sink.setChannel(channel);

		Configurables.configure(sink, context);
		Configurables.configure(channel, context);
	}

	@AfterClass public static void finalizeTestClass() {

	}

	public void testProcess() throws Exception {
		setUpClass();

		Event event = EventBuilder.withBody("2015-06-25T10:05:53.000+03:00|2015-06-08 10:05:53 AM    LogName=Security       SourceName=Microsoft Windows security auditing. EventCode=4769  EventType=0     Type=Information        ComputerName=Fs-DC-01.Fortscale.dom     TaskCategory=Kerberos Service Ticket Operations OpCode=Info     RecordNumber=927247275  Keywords=Audit Success  Message=A Kerberos service ticket was requested.                Account Information:            Account Name:           ", Charsets.UTF_8);

		sink.start();

		Transaction transaction = channel.getTransaction();

		transaction.begin();
		for (int i = 0; i < 10; i++) {
			channel.put(event);
		}
		transaction.commit();
		transaction.close();

		for (int i = 0; i < 5; i++) {
			Sink.Status status = sink.process();
			Assert.assertEquals(Sink.Status.READY, status);
		}

		sink.stop();
	}
}