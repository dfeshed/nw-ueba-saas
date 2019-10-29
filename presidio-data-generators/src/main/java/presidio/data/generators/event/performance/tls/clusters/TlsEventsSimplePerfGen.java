package presidio.data.generators.event.performance.tls.clusters;

import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.dictionary.SingleWordCyclicGenerator;
import presidio.data.generators.common.list.random.RandomRangeCompanyGen;
import presidio.data.generators.common.perf.tls.*;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.common.random.Md5RandomGenerator;
import presidio.data.generators.common.random.RandomIntegerGenerator;
import presidio.data.generators.common.random.RandomStringGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.tls.HostnameRangeAllocator;
import presidio.data.generators.event.tls.Ipv4RangeAllocator;
import presidio.data.generators.event.tls.LocationRangeAllocator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TlsEventsSimplePerfGen extends AbstractEventGenerator<TlsEvent> {

    private static AtomicInteger UNIQUE_ID_COUNTER = new AtomicInteger(0);
    private TlsPerfClusterParams params;
    private final int UNIQUE_ID;

    private final Supplier<RandomRangeCompanyGen> sslCaGenSupplier = () -> {
        RandomRangeCompanyGen gen = new RandomRangeCompanyGen(100, 200);
        gen.formatter = String::toLowerCase;
        return gen;
    };




    private IBaseGenerator<Integer> dstPortGen;
    private IBaseGenerator<String> ja3Gen;
    private IBaseGenerator<String> sslSubjectGen;
    private IBaseGenerator<String> dstOrgGen;
    private IBaseGenerator<String> srcNetnameGen;
    private IBaseGenerator<String> srcIpGenerator;

    public final LocationRangeAllocator locationGen = new LocationRangeAllocator();
    public final HostnameRangeAllocator hostnameGen = new HostnameRangeAllocator();

    private final Ipv4RangeAllocator dstIpGenerator = new Ipv4RangeAllocator();
    private IBaseGenerator<String> dstAsnGenerator = new RandomStringGenerator(5, 8);
    private IBaseGenerator<String> sslCaGenerator = sslCaGenSupplier.get();
    private IBaseGenerator<String> ja3sGenerator = new Md5RandomGenerator();
    private IBaseGenerator<String> dataSourceGenerator = new RandomStringGenerator(6, 7);
    private IBaseGenerator<String> dstNetnameGen = new SingleWordCyclicGenerator(201);
    private IBaseGenerator<String> eventIdGenerator = new Md5RandomGenerator();
    private IBaseGenerator<Long> numOfBytesSentGenerator = new GaussianLongGenerator(500000.0D, 100000.0D);
    private IBaseGenerator<Long> numOfBytesReceivedGenerator = new GaussianLongGenerator(500000.0D, 100000.0D);
    private IBaseGenerator<Integer> srcPortGenerator = new RandomIntegerGenerator(0, 9999);
    private IBaseGenerator<Integer> sessionSplitGenerator = new FixedValueGenerator<>(0);


    public TlsEventsSimplePerfGen(TlsPerfClusterParams params) {
        this.params = params;
        UNIQUE_ID = UNIQUE_ID_COUNTER.addAndGet(1);

        dstIpGenerator.nextRangeRandom(params.dstIpSize);

        dstPortGen = new DstPortPerfGen(UNIQUE_ID, params.dstPortSize);
        ja3Gen = new Ja3PerfGen(UNIQUE_ID, params.ja3Size);
        sslSubjectGen = new SslSubjectPerfGen(UNIQUE_ID, params.sslSubjectSize);
        dstOrgGen = new DstOrgPerfGen(UNIQUE_ID, params.dstOrgSize);
        srcNetnameGen = new SrcNetnamePerfGen(UNIQUE_ID, params.srcNetnameSize);
        srcIpGenerator = new Ipv4PerfGen(UNIQUE_ID, params.srcIpSize);

        locationGen.nextRangeGenCyclic(params.locationSize);
        hostnameGen.nextRangeGenCyclic(params.hostnameSize);
    }


    public TlsEventsSimplePerfGen copy() {
        TlsEventsSimplePerfGen copyGen = new TlsEventsSimplePerfGen(this.params);
        copyGen.setTimeGenerator(this.getTimeGenerator());
        copyGen.hostnameGen.setGenerator(this.hostnameGen.getGenerator());
        copyGen.setSrcPortGenerator(this.getSrcPortGenerator());

        copyGen.dstPortGen = this.dstPortGen;
        copyGen.ja3Gen = this.ja3Gen;
        copyGen.sslSubjectGen = this.sslSubjectGen;
        copyGen.dstOrgGen = this.dstOrgGen;
        copyGen.srcNetnameGen = this.srcNetnameGen;
        copyGen.srcIpGenerator = this.srcIpGenerator;

        copyGen.setDstNetnameGen(this.getDstNetnameGen());
        copyGen.locationGen.setGenerator(this.locationGen.getGenerator());
        copyGen.setDataSourceGenerator(this.getDataSourceGenerator());
        copyGen.setDstAsnGenerator(this.getDstAsnGenerator());
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
        tlsEvent.setSourceIp(srcIpGenerator.getNext());
        tlsEvent.setDestinationOrganization(dstOrgGen.getNext());
        tlsEvent.setDestinationASN(dstAsnGenerator.getNext());
        tlsEvent.setNumOfBytesSent(numOfBytesSentGenerator.getNext());
        tlsEvent.setNumOfBytesReceived(numOfBytesReceivedGenerator.getNext());
        tlsEvent.setSourceNetname(srcNetnameGen.getNext());
        tlsEvent.setDestinationNetname(dstNetnameGen.getNext());
        tlsEvent.setJa3(ja3Gen.getNext());
        tlsEvent.setJa3s(ja3sGenerator.getNext());
        tlsEvent.setDataSource(dataSourceGenerator.getNext());
        tlsEvent.setDirection(NETWORK_DIRECTION_TYPE.OUTBOUND);
        tlsEvent.setDestinationPort(dstPortGen.getNext());
        tlsEvent.setSourcePort(srcPortGenerator.getNext());
        tlsEvent.setSrcLocation(locationGen.getGenerator().getNext());
        tlsEvent.setDstLocation(locationGen.getGenerator().getNext());
        tlsEvent.setSslSubject(sslSubjectGen.getNext());
        tlsEvent.setSslCa(sslCaGenerator.nextValues(2));
        tlsEvent.setSessionSplit(sessionSplitGenerator.getNext());
        tlsEvent.setIsSelfSigned(false);

        return tlsEvent;
    }


    public IBaseGenerator<String> getJa3Gen() {
        return ja3Gen;
    }

    public void setJa3Gen(IBaseGenerator<String> ja3Gen) {
        this.ja3Gen = ja3Gen;
    }

    public IBaseGenerator<String> getSslSubjectGen() {
        return sslSubjectGen;
    }

    public void setSslSubjectGen(IBaseGenerator<String> sslSubjectGen) {
        this.sslSubjectGen = sslSubjectGen;
    }

    public IBaseGenerator<String> getDstOrgGen() {
        return dstOrgGen;
    }

    public void setDstOrgGen(IBaseGenerator<String> dstOrgGen) {
        this.dstOrgGen = dstOrgGen;
    }

    public IBaseGenerator<String> getSrcNetnameGen() {
        return srcNetnameGen;
    }

    public void setSrcNetnameGen(IBaseGenerator<String> srcNetnameGen) {
        this.srcNetnameGen = srcNetnameGen;
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
