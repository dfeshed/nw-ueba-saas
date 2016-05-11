package fortscale.flume;

import com.google.common.base.Preconditions;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import org.apache.flume.Context;
import org.apache.flume.CounterGroup;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.apache.flume.source.SyslogSourceConfigurationConstants;
import org.apache.flume.source.SyslogUtils;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The class is an implementation of Flume API for custom source
 * The class receive events from syslog and send it to Flume channel.
 * The class supports decompression, using LZ4
 * For more information about Flume sources, please see the official documentation:
 * https://flume.apache.org/FlumeUserGuide.html#flume-sources
 */
public class SyslogTcpSource extends AbstractSource implements EventDrivenSource, Configurable {

    // Key to hold the compression flag in the config
    public static final String CONFIG_USE_COMPRESSION = "useCompression";
    // Key to hold the event delimiter in the config
    public static final String CONFIG_EVENT_DELIMITER = "eventDelimiter";

    private static final Logger logger = LoggerFactory.getLogger(SyslogTcpSource.class);
    private int port;
    private String host = null;
    private Channel nettyChannel;
    private Integer eventSize;
    private Map<String, String> formaterProp;
    private CounterGroup counterGroup = new CounterGroup();
    private Set<String> keepFields;
    private boolean isUseCompression;
    private byte[] eventsDelimiter;

    @Override public void start() {
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

        // Server to receive syslog messages
        ServerBootstrap serverBootstrap = new ServerBootstrap(factory);
        serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override public ChannelPipeline getPipeline() {
                SyslogTcpHandler handler = new SyslogTcpHandler();
                handler.setEventSize(eventSize);
                handler.setFormater(formaterProp);
                handler.setKeepFields(keepFields);
                handler.setCompressionFlag(isUseCompression);
                handler.setEventsDelimiter(eventsDelimiter);
                return Channels.pipeline(handler);
            }
        });

        logger.info("Syslog TCP Source starting...");

        if (host == null) {
            nettyChannel = serverBootstrap.bind(new InetSocketAddress(port));
        } else {
            nettyChannel = serverBootstrap.bind(new InetSocketAddress(host, port));
        }

        super.start();
    }

    @Override public void stop() {
        logger.info("Syslog TCP Source stopping...");
        logger.info("Metrics:{}", counterGroup);

        if (nettyChannel != null) {
            nettyChannel.close();
            try {
                nettyChannel.getCloseFuture().await(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.warn("netty server stop interrupted", e);
            } finally {
                nettyChannel = null;
            }
        }

        super.stop();
    }

    @Override public void configure(Context context) {
        Configurables.ensureRequiredNonNull(context, SyslogSourceConfigurationConstants.CONFIG_PORT);
        port = context.getInteger(SyslogSourceConfigurationConstants.CONFIG_PORT);
        host = context.getString(SyslogSourceConfigurationConstants.CONFIG_HOST);
        eventSize = context.getInteger("eventSize", SyslogUtils.DEFAULT_SIZE);
        formaterProp = context.getSubProperties(SyslogSourceConfigurationConstants.CONFIG_FORMAT_PREFIX);
        keepFields = SyslogUtils.chooseFieldsToKeep(context.getString(SyslogSourceConfigurationConstants.CONFIG_KEEP_FIELDS, SyslogSourceConfigurationConstants.DEFAULT_KEEP_FIELDS));
        isUseCompression = context.getBoolean(CONFIG_USE_COMPRESSION);
        eventsDelimiter = context.getString(CONFIG_EVENT_DELIMITER).getBytes();
    }

    public int getSourcePort() {
        SocketAddress localAddress = nettyChannel.getLocalAddress();
        if (localAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress) localAddress;
            return addr.getPort();
        }
        return 0;
    }

    public class SyslogTcpHandler extends SimpleChannelHandler {

        private SyslogUtils syslogUtils = new SyslogUtils();

        // Events delimiter
        byte[] eventsDelimiter;

        // Compression variables
        private boolean useCompression;
        LZ4Factory factory = LZ4Factory.fastestInstance();
        LZ4SafeDecompressor decompressor;

        final static double BUFFER_OVERSIZE_FACTOR = 10;

        public void setEventsDelimiter(byte[] eventsDelimiter) {
            this.eventsDelimiter = eventsDelimiter;
        }

        public void setEventSize(int eventSize) {
            syslogUtils.setEventSize(eventSize);
        }

        public void setKeepFields(Set<String> keepFields) {
            syslogUtils.setKeepFields(keepFields);
        }

        public void setFormater(Map<String, String> prop) {
            syslogUtils.addFormats(prop);
        }

        public void setCompressionFlag(boolean isUseCompression) {
            useCompression = isUseCompression;
            if (useCompression) {
                decompressor = factory.safeDecompressor();
            }
        }

        @Override public void messageReceived(ChannelHandlerContext ctx, MessageEvent mEvent) {

            ChannelBuffer buff = (ChannelBuffer) mEvent.getMessage();
            if (buff.readable()) {
                try {
                    // Read the input data
                    byte[] outBuffer = buff.array();
                    if (outBuffer != null) {
                        // trim zeros
                        outBuffer = trimData(outBuffer);

                        // Create event splitter - for splitting batch of events
                        BufferSplitter splitter = new BufferSplitter(handleEventCompression(outBuffer), eventsDelimiter);

                        // Buffer to hold the current event
                        byte[] eventData;

                        // Iterate the events and add them to the channel
                        while ((eventData = splitter.getNextEvent()) != null) {
                            Event e = EventBuilder.withBody(eventData);
                            getChannelProcessor().processEvent(e);
                            counterGroup.incrementAndGet("events.success");
                        }
                    }
                } catch (Exception ex) {
                    counterGroup.incrementAndGet("events.dropped");
                    logger.error("Error writting to channel, event dropped", ex);
                }
            }
        }

        /**
         * Decompress the event data, if configured
         */
        private byte[] handleEventCompression(byte[] eventData) {
            if (!isUseCompression) {
                return eventData;
            }

            // Create output buffer - needs to be over-sized
            byte[] restored = new byte[(int)(eventData.length * BUFFER_OVERSIZE_FACTOR)];

            // Decompress the data
            decompressor.decompress(eventData, 0, eventData.length, restored, 0);

            // Trim zeros
            return trimData(restored);

        }

        /**
         * Trim zeros from the end of the buffer
         *
         * @param input
         * @return
         */
        private byte[] trimData(byte[] input) {
            int i = input.length;
            while (i-- > 0 && input[i] == 0) {
            }

            byte[] output = new byte[i + 1];
            System.arraycopy(input, 0, output, 0, i + 1);

            return output;
        }

    }

    /**
     * Class for handling batch of events
     * Will receive a batch of events, separate by a previously known delimiter, and separate them
     *
     * Note: this class is stateful
     */
    public class BufferSplitter {

        // Holds the events data
        byte[] data;

        // Current index
        int index;

        // The pattern that separate the events
        byte[] pattern;

        // Flag to indicate whether we have more events to read
        boolean hasNext;

        /**
         * Create new BufferSplitter
         * @param data
         * @param pattern
         */
        public BufferSplitter(byte[] data, byte[] pattern) {
            this.data = data;
            this.pattern = pattern;
            this.index = 0;
            hasNext = true;
        }

        /**
         * Read the next event
         * In case there's no more events, return null
         * @return
         */
        public byte[] getNextEvent() {

            // If we have no more events, return null
            if (!hasNext){
                return null;
            }

            // Find the index of the of the next delimiter
            int chunkIndex = indexOf(data, pattern, index);

            // If we have no more delimiters, set the flag to false and return the remaining data
            if (chunkIndex == -1) {
                hasNext = false;
                if (index == data.length) {
                    return null;
                }
                return Arrays.copyOfRange(data, index, data.length);
            }

            // Update the index to the next event
            int oldIndex = index;
            index = chunkIndex + pattern.length;

            // Copy and return the event data
            return Arrays.copyOfRange(data, oldIndex, chunkIndex);
        }

        /**
         * Return true if we have more events to read
         * @return
         */
        public boolean hasNext(){
            return hasNext;
        }

        /**
         * Find the index of 'pattern' in data, starting from 'startingIndex'.
         * In case there's no match, return -1
         * @param data
         * @param pattern
         * @param startingIndex
         * @return
         */
        private int indexOf(byte[] data, byte[] pattern, int startingIndex) {
            Preconditions.checkNotNull(data, "data");
            Preconditions.checkNotNull(pattern, "pattern");

            if (pattern.length == 0) {
                return 0;
            }

            // Main loop - look for pattern in data.
            // In case there's a mismatch, stop the inner loop and continue with the outer loop
            main:
            for (int i = startingIndex; i < data.length - pattern.length + 1; ++i) {
                for (int j = 0; j < pattern.length; ++j) {
                    if (data[i + j] != pattern[j]) {
                        continue main;
                    }
                }
                return i;
            }

            // No match was found
            return -1;
        }


    }
}
