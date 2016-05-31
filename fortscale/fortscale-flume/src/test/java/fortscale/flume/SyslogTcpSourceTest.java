package fortscale.flume;

import junit.framework.TestCase;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import org.apache.flume.*;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SyslogTcpSourceTest extends TestCase {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SyslogTcpSourceTest.class);
    private SyslogTcpSource source;
    private Channel channel;
    private static final int TEST_SYSLOG_PORT = 0;
    private final DateTime time = new DateTime();
    private final String stamp1 = time.toString();
    private final String host1 = "localhost.localdomain";
    private final String data1 = "test syslog data";
    private final String bodyWithTandH = "<10>" + stamp1 + " " + host1 + " " + data1;

    private void init() {
        source = new SyslogTcpSource();
        channel = new MemoryChannel();

        Configurables.configure(channel, new Context());

        List<Channel> channels = new ArrayList<Channel>();
        channels.add(channel);

        ChannelSelector rcs = new ReplicatingChannelSelector();
        rcs.setChannels(channels);

        source.setChannelProcessor(new ChannelProcessor(rcs));
        Context context = new Context();
        context.put("port", String.valueOf(TEST_SYSLOG_PORT));
        context.put("keepFields", "all");
        context.put("useCompression", "true");
        context.put("eventDelimiter", "!@#$%");

        source.configure(context);

    }

    /**
     * Test reading events from syslog using the Tcp source
     *
     * @throws IOException
     */
    @Test public void testSource() throws IOException, InterruptedException {
        init();
        source.start();

        LZ4Factory factory = LZ4Factory.fastestInstance();
        byte[] data = bodyWithTandH.getBytes();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] eventDelimiter = new String("!@#$%").getBytes();

        for (int i = 0; i < 10; i++) {
            outputStream.write(data);
            if (i != 9) {
                outputStream.write(eventDelimiter);
            }
        }
        final int decompressedLength = outputStream.size();

        LZ4Compressor compressor = factory.fastCompressor();
        int maxCompressedLength = compressor.maxCompressedLength(decompressedLength);
        byte[] compressed = new byte[maxCompressedLength];
        compressor.compress(outputStream.toByteArray(), 0, decompressedLength, compressed, 0, maxCompressedLength);

        // Write some message to the syslog port

        Socket syslogSocket;
        for (int i = 0; i < 5; i++) {
            syslogSocket = new Socket(InetAddress.getLocalHost(), source.getSourcePort());
            syslogSocket.getOutputStream().write(compressed);
            syslogSocket.getOutputStream().flush();
            syslogSocket.close();
        }

        List<Event> channelEvents = new ArrayList<Event>();
        Transaction txn = channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 50; i++) {
            Event e = channel.take();
            if (e == null) {
                throw new NullPointerException("Event is null");
            }
            channelEvents.add(e);
        }

        try {
            txn.commit();
        } catch (Throwable t) {
            txn.rollback();
        } finally {
            txn.close();
        }

        source.stop();

        int index = 0;
        for (Event e : channelEvents) {

            System.out.print(index++);
            Assert.assertArrayEquals(e.getBody(), data);
        }
    }
}

