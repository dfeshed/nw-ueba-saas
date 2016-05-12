package fortscale.flume;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.mozilla.universalchardet.UniversalDetector;
import org.productivity.java.syslog4j.Syslog;
import org.productivity.java.syslog4j.SyslogConfigIF;
import org.productivity.java.syslog4j.SyslogIF;
import org.productivity.java.syslog4j.SyslogRuntimeException;
import org.productivity.java.syslog4j.impl.net.tcp.TCPNetSyslogConfig;
import org.productivity.java.syslog4j.impl.net.udp.UDPNetSyslogConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The class is an implementation of Flume API for custom sink
 * The class reads events from Flume channel and send it to syslog.
 * The class supports both sending in UDP and TCP
 * The class also supports compression, using LZ4
 *
 * For more information about Flume sinks, please see the official documentation:
 * https://flume.apache.org/FlumeUserGuide.html#flume-sinks
 */
public class FsSink extends AbstractSink implements Configurable {

	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

	private String SyslogRemoteHost;
	private int SyslogMessageMaxLength;
	private int SyslogRemotePort;
	private String TransportProtocol;
	private SinkCounter SinkEventCounter;
	private boolean isUseCompression;
	private static final Logger LOG = LoggerFactory.getLogger(FsSink.class);
	private static SyslogConfigIF SyslogConfig;
	private static SyslogIF syslogger;
	private LZ4Compressor compressor;

	@Override public void configure(Context context) {
		// Extracts Data from the configuration file /etc/flume/conf/flume.conf

		// extract batch size
		int batchsize = context.getInteger("batchSize");
		if (1 != batchsize) {
			LOG.error("Batch size configured is different than 1, stopping sink");
			stop();
		}
		// Remote host ip address or hostname
		String sysloghost = context.getString("sysloghost");

		// Remote syslog port
		int syslogport = context.getInteger("syslogport");

		// Set Syslog message size, default is set to 2048 since it's rsyslog's default size!
		int messagemaxlength = context.getInteger("messagemaxlength", 2048);

		// Read the sending protocol from the config
		String transportProtocol = context.getString("transportProtocol", "TCP");

		// Whether to use compression
		boolean isUseCompression = context.getBoolean("useCompression", false);

		// this section applies the configuration to th private variables to be used in the class
		this.SyslogRemoteHost = sysloghost;
		this.SyslogRemotePort = syslogport;
		this.TransportProtocol = transportProtocol;
		this.SyslogMessageMaxLength = messagemaxlength;
		this.isUseCompression = isUseCompression;

		// Initializes the Sink counter
		SinkEventCounter = new SinkCounter("FsSink " + this.getName());

		// If compression flag is set, init the class compressor
		if (isUseCompression) {
			LZ4Factory factory = LZ4Factory.fastestInstance();
			compressor = factory.fastCompressor();
		}
	}

	@Override public void start() {

		// set transport protocol
		if (Objects.equals(this.TransportProtocol.toLowerCase(), "tcp")) {
			SyslogConfig = new TCPNetSyslogConfig();
		} else {
			SyslogConfig = new UDPNetSyslogConfig();
		}

		// Initialize syslogger with the settings from the flume conf file (retrieved in configure function)
		SyslogConfig.setMaxMessageLength(SyslogMessageMaxLength);
		SyslogConfig.setHost(SyslogRemoteHost);
		SyslogConfig.setPort(SyslogRemotePort);
		SyslogConfig.setSendLocalName(false);
		SyslogConfig.setSendLocalTimestamp(false);
		try {
			syslogger = Syslog.createInstance("Fs" + this.getName(), SyslogConfig);
			LOG.info("Syslog Fortscale Successfully initialized!");
		} catch (SyslogRuntimeException ex) {
			LOG.error(ex.getMessage());
		}

		SinkEventCounter.start();
		LOG.info("Starting FS Sink " + getName());

	}

	@Override public void stop() {
		SinkEventCounter.stop();
		LOG.info("Stopping FS Sink " + getName());
		syslogger.shutdown();
	}

	@Override public Status process() throws EventDeliveryException {
		Status status = null;

		// Start transaction
		Channel ch = getChannel();
		Transaction txn = ch.getTransaction();
		txn.begin();
		try {
			// This try clause includes whatever Channel operations you want to do

			Event event = ch.take();
			if (event != null) {

				// Calls the function which parses event and send the raw log.
				decodeData(event);

			}

			txn.commit();
			SinkEventCounter.addToEventDrainSuccessCount(1);
			status = Status.READY;
		} catch (Throwable t) {
			txn.rollback();

			// Log exception, handle individual exceptions as needed
			status = Status.BACKOFF;

			// re-throw all Errors
			if (t instanceof Error) {
				throw (Error) t;
			}
		} finally {
			txn.close();
		}
		return status;
	}

	/**
	 * Receive Flume event and sent it to syslog
	 * @param event
	 * @throws IOException
	 */
	private void decodeData(Event event) throws IOException {

		// Read data from the event
		// Compress the data if needed
		byte[] eventData = handleEventCompression(event.getBody());

		// Convert the data to String
		String logRawString = convertMessage(eventData);

		// Send the message to syslog
		if (logRawString != null && !Objects.equals(logRawString, "")) {
			sendMessage(logRawString);
		}

	}

	/**
	 * Send the message
	 * @param message
	 */
	private void sendMessage(String message) {
		syslogger.log(Syslog.LEVEL_INFO, message);
	}

	// Compress the event data, if configured
	private byte[] handleEventCompression(byte[] eventData) {
		if (!isUseCompression) {
			return eventData;
		}

		int maxCompressedLength = compressor.maxCompressedLength(eventData.length);
		byte[] compressed = new byte[maxCompressedLength];
		compressor.compress(eventData, 0, eventData.length, compressed, 0, maxCompressedLength);
		return compressed;
	}

	/**
	 *
	 * @param message
	 * @return
	 */
	private String convertMessage(byte[] message) {

		// Detect the message charset
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(message, 0, message.length);
		detector.dataEnd();
		String charset = detector.getDetectedCharset();
		detector.reset();

		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}

		// Convert the data from byte[] to string
		try {
			return new String(message, charset);
		} catch (UnsupportedEncodingException e) {
			LOG.error("Could convert event data. error: " + e.getMessage());
		}

		return null;
	}
}