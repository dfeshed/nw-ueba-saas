package presidio.data.generators.event.network;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import presidio.data.domain.Location;
import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.authenticationlocation.AuthenticationLocationCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.dictionary.CompanyNameCyclicGenerator;
import presidio.data.generators.common.dictionary.SingleWordCyclicGenerator;
import presidio.data.generators.common.random.Md5RandomGenerator;
import presidio.data.generators.common.random.RandomIntegerGenerator;
import presidio.data.generators.common.random.RandomIpGenerator;
import presidio.data.generators.common.random.RandomStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.hostname.HostnameGenerator;

import java.time.Instant;
import java.util.List;

public class NetworkEventsGenerator extends AbstractEventGenerator<NetworkEvent> {
    public static final int DEFAULT_REGULAR_PORT_BELOW = 9999;
    public static final int DEFAULT_FQDN_START_INDEX = 0;
    public static final int DEFAULT_FQDN_END_INDEX = 1500;

    // default generators:
    private IBaseGenerator<String> sslSubjectGenerator = new CompanyNameCyclicGenerator(0);
    private IBaseGenerator<String> destinationOrganizationGenerator = new CompanyNameCyclicGenerator(100);
    private IBaseGenerator<String> destinationAsnGenerator = new RandomStringGenerator(5,8);
    private IBaseGenerator<String> sslCaGenerator = new RandomStringGenerator(3,5);
    private IBaseGenerator<String>  ja3Generator = new Md5RandomGenerator();
    private IBaseGenerator<String>  ja3sGenerator = new Md5RandomGenerator();
    private IBaseGenerator<String>  dataSourceGenerator = new RandomStringGenerator(6,7);
    private IBaseGenerator<String> fqdnGenerator = new HostnameGenerator(DEFAULT_FQDN_START_INDEX,DEFAULT_FQDN_END_INDEX);
    private IBaseGenerator<String> dstIpGenerator = new RandomIpGenerator();
    private IBaseGenerator<String> sourceIpGenerator = new RandomIpGenerator();
    private IBaseGenerator<String>  sourceNetnameGen = new SingleWordCyclicGenerator(0);
    private IBaseGenerator<String>  destinationNetnameGen = new SingleWordCyclicGenerator(201);
    private IBaseGenerator<Location> locationGen = new AuthenticationLocationCyclicGenerator();
    private IBaseGenerator<String>  eventIdGenerator = new Md5RandomGenerator();
    private IBaseGenerator<Long>  numOfBytesSentGenerator =  new FixedValueGenerator<>(1024L);
    private IBaseGenerator<Long>  numOfBytesReceivedGenerator = new FixedValueGenerator<>(2048L);
    private IBaseGenerator<Integer>  sourcePortGenerator = new RandomIntegerGenerator(0, DEFAULT_REGULAR_PORT_BELOW);
    private IBaseGenerator<Integer>  destinationPortGenerator = new RandomIntegerGenerator(0, DEFAULT_REGULAR_PORT_BELOW);

    private String testMarker;

    public NetworkEventsGenerator() { }

    public NetworkEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String methodName = stackTrace[2].getMethodName();
        testMarker = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, methodName);
    }

    public NetworkEventsGenerator(ITimeGenerator timeGenerator, String testMarker) throws GeneratorException {
        super(timeGenerator);
        this.testMarker = testMarker;
    }

    public NetworkBuilderHelper modify() {
        return new NetworkBuilderHelper(this);
    }


    @Override
    public NetworkEvent generateNext() throws GeneratorException {

        Instant time = getTimeGenerator().getNext();
        String eventId = eventIdGenerator.getNext();
        List<String> fqdns = Lists.newArrayList(fqdnGenerator.getNext(), fqdnGenerator.getNext(), fqdnGenerator.getNext());
        String sourceIp = sourceIpGenerator.getNext();
        String dstIp = dstIpGenerator.getNext();
        String destinationOrganization = destinationOrganizationGenerator.getNext().toLowerCase().replaceAll("\\W"," ");
        String destinationASN = destinationAsnGenerator.getNext();
        long numOfBytesSent = numOfBytesSentGenerator.getNext();
        long numOfBytesReceived = numOfBytesReceivedGenerator.getNext();
        String sourceNetname = sourceNetnameGen.getNext();
        String destinationNetname = destinationNetnameGen.getNext();
        String ja3 = ja3Generator.getNext();
        String ja3s = ja3sGenerator.getNext();
        String dataSource = dataSourceGenerator.getNext();
        String sslSubject = sslSubjectGenerator.getNext();
        NETWORK_DIRECTION_TYPE network_direction_type = NETWORK_DIRECTION_TYPE.OUTBOUND;
        int destinationPort = destinationPortGenerator.getNext();
        int sourcePort = sourcePortGenerator.getNext();
        Location srcLocation = locationGen.getNext();
        Location dstLocation = locationGen.getNext();
        String sslCa = sslCaGenerator.getNext();
        int sessionSplit = 0;
        boolean isSelfSigned = false;

        NetworkEvent networkEvent = new NetworkEvent(time);
        networkEvent.setEventId(eventId);
        networkEvent.setFqdn(fqdns);
        networkEvent.setDstIp(dstIp);
        networkEvent.setSourceIp(sourceIp);
        networkEvent.setDestinationOrganization(destinationOrganization);
        networkEvent.setDestinationASN(destinationASN);
        networkEvent.setNumOfBytesSent(numOfBytesSent);
        networkEvent.setNumOfBytesReceived(numOfBytesReceived);
        networkEvent.setSourceNetname(sourceNetname);
        networkEvent.setDestinationNetname(destinationNetname);
        networkEvent.setJa3(ja3);
        networkEvent.setJa3s(ja3s);
        networkEvent.setDataSource(dataSource);
        networkEvent.setDirection(network_direction_type);
        networkEvent.setDestinationPort(destinationPort);
        networkEvent.setSourcePort(sourcePort);
        networkEvent.setSrcLocation(srcLocation);
        networkEvent.setDstLocation(dstLocation);
        networkEvent.setSslSubject(sslSubject);
        networkEvent.setSslCa(sslCa);
        networkEvent.setSessionSplit(sessionSplit);
        networkEvent.setIsSelfSigned(isSelfSigned);

       return networkEvent;
    }

    public IBaseGenerator<String> getSslSubjectGenerator() {
        return sslSubjectGenerator;
    }

    public void setSslSubjectGenerator(IBaseGenerator<String> sslSubjectGenerator) {
        this.sslSubjectGenerator = sslSubjectGenerator;
    }

    public IBaseGenerator<String> getDestinationOrganizationGenerator() {
        return destinationOrganizationGenerator;
    }

    public void setDestinationOrganizationGenerator(IBaseGenerator<String> destinationOrganizationGenerator) {
        this.destinationOrganizationGenerator = destinationOrganizationGenerator;
    }

    public IBaseGenerator<String> getDestinationAsnGenerator() {
        return destinationAsnGenerator;
    }

    public void setDestinationAsnGenerator(IBaseGenerator<String> destinationAsnGenerator) {
        this.destinationAsnGenerator = destinationAsnGenerator;
    }

    public IBaseGenerator<String> getSslCaGenerator() {
        return sslCaGenerator;
    }

    public void setSslCaGenerator(IBaseGenerator<String> sslCaGenerator) {
        this.sslCaGenerator = sslCaGenerator;
    }

    public IBaseGenerator<String> getJa3Generator() {
        return ja3Generator;
    }

    public void setJa3Generator(IBaseGenerator<String> ja3Generator) {
        this.ja3Generator = ja3Generator;
    }

    public IBaseGenerator<String> getJa3sGenerator() {
        return ja3sGenerator;
    }

    public void setJa3sGenerator(IBaseGenerator<String> ja3sGenerator) {
        this.ja3sGenerator = ja3sGenerator;
    }

    public IBaseGenerator<String> getFqdnGenerator() {
        return fqdnGenerator;
    }

    public IBaseGenerator<String> getDataSourceGenerator() {
        return dataSourceGenerator;
    }

    public void setDataSourceGenerator(IBaseGenerator<String> dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public void setFqdnGenerator(IBaseGenerator<String> fqdnGenerator) {
        this.fqdnGenerator = fqdnGenerator;
    }

    public IBaseGenerator<String> getSourceNetnameGen() {
        return sourceNetnameGen;
    }

    public void setSourceNetnameGen(IBaseGenerator<String> sourceNetnameGen) {
        this.sourceNetnameGen = sourceNetnameGen;
    }

    public IBaseGenerator<String> getDestinationNetnameGen() {
        return destinationNetnameGen;
    }

    public void setDestinationNetnameGen(IBaseGenerator<String> destinationNetnameGen) {
        this.destinationNetnameGen = destinationNetnameGen;
    }

    public IBaseGenerator<Location> getLocationGen() {
        return locationGen;
    }

    public void setLocationGen(IBaseGenerator<Location> locationGen) {
        this.locationGen = locationGen;
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

    public IBaseGenerator<String> getDstIpGenerator() {
        return dstIpGenerator;
    }

    public void setDstIpGenerator(IBaseGenerator<String> dstIpGenerator) {
        this.dstIpGenerator = dstIpGenerator;
    }

    public IBaseGenerator<String> getSourceIpGenerator() {
        return sourceIpGenerator;
    }

    public void setSourceIpGenerator(IBaseGenerator<String> sourceIpGenerator) {
        this.sourceIpGenerator = sourceIpGenerator;
    }

    public IBaseGenerator<Integer> getDestinationPortGenerator() {
        return destinationPortGenerator;
    }

    public void setDestinationPortGenerator(IBaseGenerator<Integer> destinationPortGenerator) {
        this.destinationPortGenerator = destinationPortGenerator;
    }

    public IBaseGenerator<Integer> getSourcePortGenerator() {
        return sourcePortGenerator;
    }

    public void setSourcePortGenerator(IBaseGenerator<Integer> sourcePortGenerator) {
        this.sourcePortGenerator = sourcePortGenerator;
    }

    public String getTestMarker() {
        return testMarker;
    }

}
