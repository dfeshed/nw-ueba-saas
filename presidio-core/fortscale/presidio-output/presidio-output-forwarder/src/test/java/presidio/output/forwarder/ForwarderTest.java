package presidio.output.forwarder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class ForwarderTest {


    public static final String[] NUMBERS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private class ConcreteForwarder extends Forwarder<String> {

        public ConcreteForwarder(ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
            super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        }

        Stream<String> getEntitiesToForward() {
            return Arrays.asList(NUMBERS).stream();
        }

        @Override
        String getId(String entity) {
            return entity;
        }

        @Override
        String buildPayload(String entity) throws Exception {
            return entity;
        }

        @Override
        Map buildHeader(String entity) throws Exception {
            return null;
        }

        @Override
        public ForwarderStrategy.PAYLOAD_TYPE getPayloadType() {
            return ForwarderStrategy.PAYLOAD_TYPE.INDICATOR;
        }
    }


    private ForwarderConfiguration forwarderConfiguration;

    private ForwarderStrategyFactory forwarderStrategyFactory;

    private MemoryStrategy memoryStrategy;


    @Before
    public void cleanup(){
        memoryStrategy = new MemoryStrategy();

        forwarderStrategyFactory = Mockito.mock(ForwarderStrategyFactory.class);
        Mockito.when(forwarderStrategyFactory.getStrategy(Mockito.anyString())).thenReturn(memoryStrategy);

        forwarderConfiguration = Mockito.mock(ForwarderConfiguration.class);
        Mockito.when(forwarderConfiguration.getForwardingStrategy(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(MemoryStrategy.MEMORY);
        Mockito.when(forwarderConfiguration.isForwardInstance(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(true);
        Mockito.when(forwarderConfiguration.getForwardBulkSize(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(1);

    }

    @Test
    public void testBatchSize1() {
        ConcreteForwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.doForward(forwarder.getEntitiesToForward(), false);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(1, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testBatchSize10() {
        Mockito.doReturn(10).when(forwarderConfiguration).getForwardBulkSize(Mockito.any());
        ConcreteForwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.doForward(forwarder.getEntitiesToForward(), false);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(10, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testBatchSize11() {
        Mockito.doReturn(11).when(forwarderConfiguration).getForwardBulkSize(Mockito.any());
        ConcreteForwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.doForward(forwarder.getEntitiesToForward(), false);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(10, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testNoDataToStream() {
        ConcreteForwarder forwarder = Mockito.spy(new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory));
        Mockito.doReturn(Arrays.asList().stream()).when(forwarder).getEntitiesToForward();
        try {
            forwarder.doForward(forwarder.getEntitiesToForward(), false);
        } catch (Exception e){
            Assert.assertNull(e);
        }
    }

    @Test
    public void testNoStrategyPluginExist() {
        Mockito.doReturn(null).when(forwarderStrategyFactory).getStrategy(Mockito.anyString());
        ConcreteForwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        try {
            forwarder.doForward(forwarder.getEntitiesToForward(), false);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testNoForwardingFlag() {
        Mockito.doReturn(false).when(forwarderConfiguration).isForwardInstance(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class));
        ConcreteForwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        try {
            forwarder.doForward(forwarder.getEntitiesToForward(), false);
        } catch (Exception e){
            Assert.assertNull(e);
        }
        Assert.assertEquals(0, memoryStrategy.allMessages.size());
    }



}
