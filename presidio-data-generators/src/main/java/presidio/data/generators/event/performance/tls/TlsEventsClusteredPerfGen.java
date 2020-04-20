package presidio.data.generators.event.performance.tls;

import presidio.data.domain.Location;
import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.TlsEvent;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.NullGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.dictionary.SingleWordCyclicGenerator;
import presidio.data.generators.common.list.random.RandomRangeCompanyGen;
import presidio.data.generators.common.perf.tls.*;
import presidio.data.generators.common.random.GaussianLongGenerator;
import presidio.data.generators.common.random.Md5RandomGenerator;
import presidio.data.generators.common.random.RandomIntegerGenerator;
import presidio.data.generators.common.random.RandomStringGenerator;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.tls.Ipv4RangeAllocator;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TlsEventsClusteredPerfGen extends AbstractEventGenerator<TlsEvent> {

    private static AtomicInteger UNIQUE_ID_COUNTER = new AtomicInteger(0);
    private final Supplier<RandomRangeCompanyGen> sslCaGenSupplier = () -> {
        RandomRangeCompanyGen gen = new RandomRangeCompanyGen(100, 200);
        gen.formatter = String::toLowerCase;
        return gen;
    };
    private final IBaseGenerator<Long> unusualTrafficGenerator = new GaussianLongGenerator(1.5e9, 10e6);

    private TlsPerfClusterParams params;
    private final int UNIQUE_ID;

    private IBaseGenerator<Integer> dstPortGen;
    private IBaseGenerator<String> ja3Gen;
    private IBaseGenerator<String> sslSubjectGen;
    private IBaseGenerator<String> dstOrgGen;
    private IBaseGenerator<String> srcNetnameGen;
    private IBaseGenerator<String> srcIpGenerator;


    private IBaseGenerator<Location> locationGen;
    private IBaseGenerator<String> hostnameGen;

    private final Ipv4RangeAllocator dstIpGenerator = new Ipv4RangeAllocator();
    private IBaseGenerator<String> dstAsnGenerator = new NullGenerator<>();
    private IBaseGenerator<String> sslCaGenerator = sslCaGenSupplier.get();
    private IBaseGenerator<String> ja3sGenerator = new Md5RandomGenerator();
    private IBaseGenerator<String> dataSourceGenerator = new RandomStringGenerator(6, 7);
    private IBaseGenerator<String> dstNetnameGen = new SingleWordCyclicGenerator(201);
    private IBaseGenerator<String> eventIdGenerator = new Md5RandomGenerator();
    private IBaseGenerator<Long> numOfBytesSentGenerator = new GaussianLongGenerator(5e5, 2e5);
    private IBaseGenerator<Long> numOfBytesReceivedGenerator = new GaussianLongGenerator(5e12, 2e11);
    private IBaseGenerator<Integer> srcPortGenerator = new RandomIntegerGenerator(0, 9999);
    private IBaseGenerator<Integer> sessionSplitGenerator = new NullGenerator<>();

    private final Random random = new Random(0);
    private Supplier<Boolean> isAnomaly = () -> random.nextDouble() <= params.getAlertsProbability();

    public TlsEventsClusteredPerfGen(TlsPerfClusterParams params) {
        this.params = params;

        timeGenerator = getDefaultTimeGen();
        UNIQUE_ID = UNIQUE_ID_COUNTER.addAndGet(1);
        dstIpGenerator.nextRangeRandom(params.getDstIpSize());

        dstPortGen = new DstPortPerfGen(UNIQUE_ID, params.getDstPortSize());
        ja3Gen = new Ja3PerfGen(UNIQUE_ID, params.getJa3Size());
        sslSubjectGen = new SslSubjectPerfGen(UNIQUE_ID, params.getSslSubjectSize());
        dstOrgGen = new DstOrgPerfGen(UNIQUE_ID, params.getDstOrgSize());
        srcNetnameGen = new SrcNetnamePerfGen(UNIQUE_ID, params.getSrcNetnameSize());
        srcIpGenerator = new Ipv4PerfGen(UNIQUE_ID, params.getSrcIpSize());
        locationGen = new LocationPrefGen(UNIQUE_ID, params.getLocationSize());
        hostnameGen = new HostNamePrefGen(UNIQUE_ID, params.getHostnameSize());
    }

    public TlsEventsClusteredPerfGen copy() {
        TlsEventsClusteredPerfGen copyGen = new TlsEventsClusteredPerfGen(this.params);

        copyGen.params = this.params;
        copyGen.timeGenerator = this.timeGenerator;

        copyGen.setSrcPortGenerator(this.getSrcPortGenerator());
        copyGen.dstPortGen = this.dstPortGen;

        copyGen.ja3Gen = this.ja3Gen;
        copyGen.sslSubjectGen = this.sslSubjectGen;
        copyGen.dstOrgGen = this.dstOrgGen;
        copyGen.srcNetnameGen = this.srcNetnameGen;
        copyGen.srcIpGenerator = this.srcIpGenerator;

        copyGen.setDstNetnameGen(this.getDstNetnameGen());
        copyGen.locationGen = this.locationGen;
        copyGen.hostnameGen = this.hostnameGen;
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

    private  MultiRangeTimeGenerator getDefaultTimeGen() {
        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();

        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(params.getRegularActivityStartHour(),0),
                LocalTime.of(params.getRegularActivityEndHour(),0),
                Duration.ofMillis(new TlsPerfUtils().activeHoursMillisBetweenEvents(10, 100, 0))));

        Instant endTime = params.getEndInstant();
        Instant startTime = params.getStartInstant();

        return new MultiRangeTimeGenerator(startTime, endTime, rangesList, null);
    }

    @Override
    public TlsEvent generateNext() throws GeneratorException {
        boolean ifAbnormalEvent = isAnomaly.get();

        Instant nextTime = ifAbnormalEvent ? setAbnormalActivityTime(timeGenerator.getNext()) : timeGenerator.getNext();
        TlsEvent tlsEvent = new TlsEvent(nextTime);

        tlsEvent.setEventId(eventIdGenerator.getNext());
        tlsEvent.setFqdn(hostnameGen.nextValues(3));
        tlsEvent.setDstIp(dstIpGenerator.getGenerator().getNext());
        tlsEvent.setSourceIp(srcIpGenerator.getNext());
        tlsEvent.setDestinationOrganization(dstOrgGen.getNext());
        tlsEvent.setDestinationASN(dstAsnGenerator.getNext());
        tlsEvent.setNumOfBytesSent(ifAbnormalEvent ? unusualTrafficGenerator.getNext() : numOfBytesSentGenerator.getNext());
        tlsEvent.setNumOfBytesReceived(numOfBytesReceivedGenerator.getNext());
        tlsEvent.setSourceNetname(srcNetnameGen.getNext());
        tlsEvent.setDestinationNetname(dstNetnameGen.getNext());
        tlsEvent.setJa3(ja3Gen.getNext());
        tlsEvent.setJa3s(ja3sGenerator.getNext());
        tlsEvent.setDataSource(dataSourceGenerator.getNext());
        tlsEvent.setDirection(NETWORK_DIRECTION_TYPE.OUTBOUND);
        tlsEvent.setDestinationPort(dstPortGen.getNext());
        tlsEvent.setSourcePort(srcPortGenerator.getNext());
        tlsEvent.setSrcLocation(locationGen.getNext());
        tlsEvent.setDstLocation(locationGen.getNext());
        tlsEvent.setSslSubject(sslSubjectGen.getNext());
        tlsEvent.setSslCa(sslCaGenerator.nextValues(2));
        tlsEvent.setSessionSplit(sessionSplitGenerator.getNext());
        tlsEvent.setIsSelfSigned(false);

        return tlsEvent;
    }

    private Instant setAbnormalActivityTime(Instant normalActivityTime) {
        int abnormalHour =  ThreadLocalRandom.current().nextInt(params.getAbnormalActivityStartHour(), params.getAbnormalActivityEndHour());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(normalActivityTime, ZoneOffset.UTC).withHour(abnormalHour);
        return localDateTime.toInstant(ZoneOffset.UTC);
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
