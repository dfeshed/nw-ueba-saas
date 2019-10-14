package presidio.data.generators.event.tls;

import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.dictionary.SingleWordCyclicGenerator;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.common.random.Md5RandomGenerator;
import presidio.data.generators.common.random.RandomIntegerGenerator;
import presidio.data.generators.common.random.RandomStringGenerator;
import presidio.data.generators.event.AbstractEventGenerator;

public class TlsRangeEventsGen extends AbstractEventGenerator<TlsEvent> {

    private final int DEFAULT_RANGE;

    public final HostnameRangeAllocator hostnameGen = new HostnameRangeAllocator();
    public final DstPortRangeAllocator dstPortGen = new DstPortRangeAllocator();
    public final Ja3RangeAllocator ja3Gen = new Ja3RangeAllocator();
    public final SslSubjectRangeAllocator sslSubjectGen = new SslSubjectRangeAllocator();
    public final DstOrgRangeAllocator dstOrgGen = new DstOrgRangeAllocator();
    public final SrcNetnameRangeAllocator srcNetnameGen = new SrcNetnameRangeAllocator();
    public final LocationRangeAllocator locationGen = new LocationRangeAllocator();
    public final Ipv4RangeAllocator srcIpGenerator = new Ipv4RangeAllocator();


    private final Ipv4RangeAllocator dstIpGenerator = new Ipv4RangeAllocator();
    private IBaseGenerator<String> dstAsnGenerator = new RandomStringGenerator(5, 8);
    private IBaseGenerator<String> sslCaGenerator = new RandomStringGenerator(3, 5);
    private IBaseGenerator<String> ja3sGenerator = new Md5RandomGenerator();
    private IBaseGenerator<String> dataSourceGenerator = new RandomStringGenerator(6, 7);
    private IBaseGenerator<String> dstNetnameGen = new SingleWordCyclicGenerator(201);
    private IBaseGenerator<String> eventIdGenerator = new Md5RandomGenerator();
    private IBaseGenerator<Long> numOfBytesSentGenerator = new GaussianLongGenerator(500000.0D, 100000.0D);
    private IBaseGenerator<Long> numOfBytesReceivedGenerator = new GaussianLongGenerator(500000.0D, 100000.0D);
    private IBaseGenerator<Integer> srcPortGenerator = new RandomIntegerGenerator(0, 9999);
    private IBaseGenerator<Integer> sessionSplitGenerator = new FixedValueGenerator<>(0);

    
    public TlsRangeEventsGen(int defaultRange) {
        DEFAULT_RANGE = defaultRange;

        dstIpGenerator.nextRangeRandom(100);
        hostnameGen.nextRangeGenCyclic(DEFAULT_RANGE);
        dstPortGen.nextRangeGenCyclic(DEFAULT_RANGE);
        ja3Gen.nextRangeGenCyclic(DEFAULT_RANGE);
        sslSubjectGen.nextRangeGenCyclic(DEFAULT_RANGE);
        dstOrgGen.nextRangeGenCyclic(DEFAULT_RANGE);
        srcNetnameGen.nextRangeGenCyclic(DEFAULT_RANGE);
        locationGen.nextRangeGenCyclic(DEFAULT_RANGE);
        srcIpGenerator.nextRangeGenCyclic(DEFAULT_RANGE);
    }

    private TlsRangeEventsGen() {
        DEFAULT_RANGE = -1;
    }

    public TlsRangeEventsGen copy() {
        TlsRangeEventsGen copyGen = new TlsRangeEventsGen();
        copyGen.setTimeGenerator(this.getTimeGenerator());
        copyGen.hostnameGen.setGenerator(this.hostnameGen.getGenerator());
        copyGen.setSrcPortGenerator(this.getSrcPortGenerator());
        copyGen.dstPortGen.setGenerator(this.dstPortGen.getGenerator());
        copyGen.ja3Gen.setGenerator(this.ja3Gen.getGenerator());
        copyGen.sslSubjectGen.setGenerator(this.sslSubjectGen.getGenerator());
        copyGen.dstOrgGen.setGenerator(this.dstOrgGen.getGenerator());
        copyGen.srcNetnameGen.setGenerator(this.srcNetnameGen.getGenerator());
        copyGen.setDstNetnameGen(this.getDstNetnameGen());
        copyGen.locationGen.setGenerator(this.locationGen.getGenerator());
        copyGen.setDataSourceGenerator(this.getDataSourceGenerator());
        copyGen.setDstAsnGenerator(this.getDstAsnGenerator());
        copyGen.srcIpGenerator.setGenerator(this.srcIpGenerator.getGenerator());
        copyGen.dstIpGenerator.setGenerator(this.dstIpGenerator.getGenerator());
        copyGen.setNumOfBytesSentGenerator(this.getNumOfBytesSentGenerator());
        copyGen.setNumOfBytesReceivedGenerator(this.getNumOfBytesReceivedGenerator());
        copyGen.setJa3sGenerator(this.getJa3sGenerator());
        copyGen.setSslCaGenerator(this.getSslCaGenerator());
        copyGen.setSessionSplitGenerator(this.getSessionSplitGenerator());
        return copyGen;
    }
    
    
    
    @Override
    public TlsEvent generateNext() throws GeneratorException {
        TlsEvent tlsEvent = new TlsEvent(timeGenerator.getNext());
        tlsEvent.setEventId(eventIdGenerator.getNext());
        tlsEvent.setFqdn(hostnameGen.getGenerator().nextValues(3));
        tlsEvent.setDstIp(dstIpGenerator.getGenerator().getNext());
        tlsEvent.setSourceIp(srcIpGenerator.getGenerator().getNext());
        tlsEvent.setDestinationOrganization(dstOrgGen.getGenerator().getNext());
        tlsEvent.setDestinationASN(dstAsnGenerator.getNext());
        tlsEvent.setNumOfBytesSent(numOfBytesSentGenerator.getNext());
        tlsEvent.setNumOfBytesReceived(numOfBytesReceivedGenerator.getNext());
        tlsEvent.setSourceNetname(srcNetnameGen.getGenerator().getNext());
        tlsEvent.setDestinationNetname(dstNetnameGen.getNext());
        tlsEvent.setJa3(ja3Gen.getGenerator().getNext());
        tlsEvent.setJa3s(ja3sGenerator.getNext());
        tlsEvent.setDataSource(dataSourceGenerator.getNext());
        tlsEvent.setDirection(NETWORK_DIRECTION_TYPE.OUTBOUND);
        tlsEvent.setDestinationPort(dstPortGen.getGenerator().getNext());
        tlsEvent.setSourcePort(srcPortGenerator.getNext());
        tlsEvent.setSrcLocation(locationGen.getGenerator().getNext());
        tlsEvent.setDstLocation(locationGen.getGenerator().getNext());
        tlsEvent.setSslSubject(sslSubjectGen.getGenerator().getNext());
        tlsEvent.setSslCa(sslCaGenerator.getNext());
        tlsEvent.setSessionSplit(sessionSplitGenerator.getNext());
        tlsEvent.setIsSelfSigned(false);

        return tlsEvent;
    }

    public void resetCounters() {
        hostnameGen.reset();
        dstPortGen.reset();
        ja3Gen.reset();
        sslSubjectGen.reset();
        dstOrgGen.reset();
        srcNetnameGen.reset();
        locationGen.reset();
        srcIpGenerator.reset();
        dstIpGenerator.reset();
    }

    public IBaseGenerator<String> getDstAsnGenerator() {
        return dstAsnGenerator;
    }

    public void setDstAsnGenerator(IBaseGenerator<String> dstAsnGenerator) {
        this.dstAsnGenerator = dstAsnGenerator;
    }

    public IBaseGenerator<String> getSslCaGenerator() {
        return sslCaGenerator;
    }

    public void setSslCaGenerator(IBaseGenerator<String> sslCaGenerator) {
        this.sslCaGenerator = sslCaGenerator;
    }

    public IBaseGenerator<String> getJa3sGenerator() {
        return ja3sGenerator;
    }

    public void setJa3sGenerator(IBaseGenerator<String> ja3sGenerator) {
        this.ja3sGenerator = ja3sGenerator;
    }

    public IBaseGenerator<String> getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IBaseGenerator<String> dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public IBaseGenerator<String> getEventIdGenerator() {
        return eventIdGenerator;
    }

    public void setEventIdGenerator(IBaseGenerator<String> eventIdGenerator) {
        this.eventIdGenerator = eventIdGenerator;
    }

    public IBaseGenerator<Long> getNumOfBytesSentGenerator() {
        return numOfBytesSentGenerator;
    }

    public void setNumOfBytesSentGenerator(IBaseGenerator<Long> numOfBytesSentGenerator) {
        this.numOfBytesSentGenerator = numOfBytesSentGenerator;
    }

    public IBaseGenerator<Long> getNumOfBytesReceivedGenerator() {
        return numOfBytesReceivedGenerator;
    }

    public void setNumOfBytesReceivedGenerator(IBaseGenerator<Long> numOfBytesReceivedGenerator) {
        this.numOfBytesReceivedGenerator = numOfBytesReceivedGenerator;
    }

    public IBaseGenerator<Integer> getSrcPortGenerator() {
        return srcPortGenerator;
    }

    public void setSrcPortGenerator(IBaseGenerator<Integer> srcPortGenerator) {
        this.srcPortGenerator = srcPortGenerator;
    }

    public IBaseGenerator<Integer> getSessionSplitGenerator() {
        return sessionSplitGenerator;
    }

    public void setSessionSplitGenerator(IBaseGenerator<Integer> sessionSplitGenerator) {
        this.sessionSplitGenerator = sessionSplitGenerator;
    }


    public IBaseGenerator<String> getDstNetnameGen() {
        return dstNetnameGen;
    }

    public void setDstNetnameGen(IBaseGenerator<String> dstNetnameGen) {
        this.dstNetnameGen = dstNetnameGen;
    }
}
