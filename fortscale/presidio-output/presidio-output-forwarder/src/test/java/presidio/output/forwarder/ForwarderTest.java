package presidio.output.forwarder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import presidio.output.forwarder.strategy.ForwarderConfiguration;
import presidio.output.forwarder.strategy.ForwarderStrategy;
import presidio.output.forwarder.strategy.ForwarderStrategyFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ForwarderTest {


    public static final String[] NUMBERS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private class ConcreteForwarder extends Forwarder<String> {

        public ConcreteForwarder(ForwarderConfiguration forwarderStrategyConfiguration, ForwarderStrategyFactory forwarderStrategyFactory) {
            super(forwarderStrategyConfiguration, forwarderStrategyFactory);
        }

        @Override
        Stream<String> getEntitiesToForward(Instant startDate, Instant endDate, String entityType, List<String> alertIds) {
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
        Mockito.when(forwarderConfiguration.isForwardEntity(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(true);
        Mockito.when(forwarderConfiguration.getForwardBulkSize(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class))).thenReturn(1);

    }

    @Test
    public void testBatchSize1() {
        Forwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.forward(Instant.now(), Instant.now(), "", null);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(1, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testBatchSize10() {
        Mockito.doReturn(10).when(forwarderConfiguration).getForwardBulkSize(Mockito.any());
        Forwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.forward(Instant.now(), Instant.now(), "", null);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(10, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testBatchSize11() {
        Mockito.doReturn(11).when(forwarderConfiguration).getForwardBulkSize(Mockito.any());
        Forwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        forwarder.forward(Instant.now(), Instant.now(), "", null);
        Assert.assertEquals(10, memoryStrategy.allMessages.size());
        Assert.assertArrayEquals(NUMBERS,memoryStrategy.allMessages.stream().map(message -> message.getPayload()).toArray());
        Assert.assertEquals(10, memoryStrategy.lastBatchMessages.size());
    }

    @Test
    public void testNoDataToStream() {
        Forwarder forwarder = Mockito.spy(new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory));
        Mockito.doReturn(Arrays.asList().stream()).when(forwarder).getEntitiesToForward(Mockito.any(Instant.class),Mockito.any(Instant.class), Mockito.any(String.class), Mockito.any(List.class));
        try {
            forwarder.forward(Instant.now(), Instant.now(), "", null);
        } catch (Exception e){
            Assert.assertNull(e);
        }
    }

    @Test
    public void testNoStrategyPluginExist() {
        Mockito.doReturn(null).when(forwarderStrategyFactory).getStrategy(Mockito.anyString());
        Forwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        try {
            forwarder.forward(Instant.now(), Instant.now(), "", null);
        } catch (Exception e) {
            Assert.assertNotNull(e);
        }
    }

    @Test
    public void testNoForwardingFlag() {
        Mockito.doReturn(false).when(forwarderConfiguration).isForwardEntity(Mockito.any(ForwarderStrategy.PAYLOAD_TYPE.class));
        Forwarder forwarder = new ConcreteForwarder(forwarderConfiguration, forwarderStrategyFactory);
        try {
            forwarder.forward(Instant.now(), Instant.now(), "", null);
        } catch (Exception e){
            Assert.assertNull(e);
        }
        Assert.assertEquals(0, memoryStrategy.allMessages.size());
    }



}
