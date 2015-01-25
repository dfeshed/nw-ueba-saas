package fortscale.flume;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.serialization.EventSerializer;
import org.apache.flume.serialization.EventSerializerFactory;
import org.apache.flume.sink.AbstractSink;
import org.apache.flume.sink.RollingFileSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Flume rolling file sink that supports receiving the file name pattern as parameter
 */
public class FileNameRollingFileSink extends AbstractSink implements Configurable {

	private static final Logger logger = LoggerFactory.getLogger(FileNameRollingFileSink.class);

	private static final long defaultRollInterval = 30;
	private static final int defaultBatchSize = 100;

	private int batchSize = defaultBatchSize;

	private File directory;
	private long rollInterval;
	private OutputStream outputStream;
	private ScheduledExecutorService rollService;

	private String serializerType;
	private Context serializerContext;
	private EventSerializer serializer;

	private SinkCounter sinkCounter;

	private PathGenerator pathController;
	private volatile boolean shouldRotate;

	private String fileSuffix;
	private String filePrefix;

	public FileNameRollingFileSink() {
		pathController = new PathGenerator();
		shouldRotate = false;
	}


	@Override
	public void configure(Context context) {

		String directory = context.getString("sink.directory");
		rollInterval = context.getLong("sink.rollInterval", defaultRollInterval);
		fileSuffix = context.getString("sink.fileSuffix");
		filePrefix = context.getString("sink.filePrefix");
		serializerType = context.getString("sink.serializer", "TEXT");
		serializerContext = new Context(context.getSubProperties("sink." + EventSerializer.CTX_PREFIX));
		batchSize = context.getInteger("sink.batchSize", defaultBatchSize);

		this.directory = new File(directory);
		if (sinkCounter == null) {
			sinkCounter = new SinkCounter(getName());
		}
	}

	@Override
	public void start() {
		logger.info("Starting {}...", this);
		super.start();
		sinkCounter.start();

		pathController.setBaseDirectory(directory);
		pathController.setFilePrefix(filePrefix);
		pathController.setFileSuffix(fileSuffix);

		if(rollInterval > 0){
			rollService = Executors.newScheduledThreadPool(1,
					new ThreadFactoryBuilder().setNameFormat(
							"rollingFileSink-roller-" + Thread.currentThread().getId() + "-%d").build());

			/*
			 * Every N seconds, mark that it's time to rotate. We purposefully do NOT
			 * touch anything other than the indicator flag to avoid error handling
			 * issues (e.g. IO exceptions occuring in two different threads.
			 * Resist the urge to actually perform rotation in a separate thread!
			 */
			rollService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        logger.debug("Marking time to rotate file {}", pathController.getCurrentFile());
                        shouldRotate = true;
                    }
			    }, rollInterval, rollInterval, TimeUnit.SECONDS);
		} else{
			logger.warn("RollInterval is not valid, file rolling will not happen.");
		}
		logger.info("RollingFileSink {} started.", getName());
	}

	@Override
	public Status process() throws EventDeliveryException {
		if (shouldRotate) {
			logger.debug("Time to rotate {}", pathController.getCurrentFile());

			if (outputStream != null) {
				logger.debug("Closing file {}", pathController.getCurrentFile());

				try {
					serializer.flush();
					serializer.beforeClose();
					outputStream.close();
					sinkCounter.incrementConnectionClosedCount();
					shouldRotate = false;
				} catch (IOException e) {
					sinkCounter.incrementConnectionFailedCount();
					throw new EventDeliveryException("Unable to rotate file "
							+ pathController.getCurrentFile() + " while delivering event", e);
				} finally {
					serializer = null;
					outputStream = null;
				}

                // rotate file in path controller, this in turn will get rid of the .part suffix
				pathController.rotate();
			}
		}

		if (outputStream == null) {
			File currentFile = pathController.getCurrentFile();
			logger.debug("Opening output stream for file {}", currentFile);
			try {
				outputStream = new BufferedOutputStream(
						new FileOutputStream(currentFile));
				serializer = EventSerializerFactory.getInstance(
                        serializerType, serializerContext, outputStream);
				serializer.afterCreate();
				sinkCounter.incrementConnectionCreatedCount();
			} catch (IOException e) {
				sinkCounter.incrementConnectionFailedCount();
				throw new EventDeliveryException("Failed to open file "
						+ pathController.getCurrentFile() + " while delivering event", e);
			}
		}

		Channel channel = getChannel();
		Transaction transaction = channel.getTransaction();
		Event event = null;
		Status result = Status.READY;

		try {
			transaction.begin();
			int eventAttemptCounter = 0;
			for (int i = 0; i < batchSize; i++) {
				event = channel.take();
				if (event != null) {
					sinkCounter.incrementEventDrainAttemptCount();
					eventAttemptCounter++;
					serializer.write(event);
				} else {
					// No events found, request back-off semantics from runner
					result = Status.BACKOFF;
					break;
				}
			}
			serializer.flush();
			outputStream.flush();
			transaction.commit();
			sinkCounter.addToEventDrainSuccessCount(eventAttemptCounter);
		} catch (Exception ex) {
			transaction.rollback();
			throw new EventDeliveryException("Failed to process transaction", ex);
		} finally {
			transaction.close();
		}

		return result;
	}

	@Override
	public void stop() {
		logger.info("FileNameRollingFileSink sink {} stopping...", getName());
		super.stop();
		sinkCounter.stop();

		if (outputStream != null) {
			logger.debug("Closing file {}", pathController.getCurrentFile());

			try {
				serializer.flush();
				serializer.beforeClose();
				outputStream.close();

				// rename closed file to remove extension
                pathController.rotate();

				sinkCounter.incrementConnectionClosedCount();
			} catch (IOException e) {
				sinkCounter.incrementConnectionFailedCount();
				logger.error("Unable to close output stream. Exception follows.", e);
			} finally {
				outputStream = null;
				serializer = null;
			}
		}
		if(rollInterval > 0){
			rollService.shutdown();

			while (!rollService.isTerminated()) {
				try {
					rollService.awaitTermination(1, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					logger.debug("Interrupted while waiting for roll service to stop. Please report this.", e);
				}
			}
		}
		logger.info("FileNameRollingFileSink sink {} stopped. Event metrics: {}", getName(), sinkCounter);
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public long getRollInterval() {
		return rollInterval;
	}

	public void setRollInterval(long rollInterval) {
		this.rollInterval = rollInterval;
	}

}
