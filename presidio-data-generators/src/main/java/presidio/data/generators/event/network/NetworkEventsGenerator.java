package presidio.data.generators.event.network;

import presidio.data.domain.Location;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.event.Entity;
import presidio.data.domain.event.network.NETWORK_DIRECTION_TYPE;
import presidio.data.domain.event.network.NetworkEvent;
import presidio.data.generators.FixedValueGenerator;
import presidio.data.generators.IBaseGenerator;
import presidio.data.generators.authenticationlocation.AuthenticationLocationCyclicGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.dictionary.DictionaryWordGenerator;
import presidio.data.generators.common.md5.Md5RandomGenerator;
import presidio.data.generators.ssl_subject.SslSubjectGenerator;
import presidio.data.generators.entity.*;
import presidio.data.generators.event.AbstractEventGenerator;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;

import java.time.Instant;

public class NetworkEventsGenerator extends AbstractEventGenerator {

    private IBaseGenerator<String> sslSubjectGenerator = new SslSubjectGenerator(0,100);
    private IBaseGenerator<String>  ja3Generator = new Md5RandomGenerator();
    private IBaseGenerator<Entity> entityGenerator = new EntityGenerator(ja3Generator,sslSubjectGenerator);
    private IBaseGenerator<String> dataSourceGenerator = new FixedValueGenerator<>("TLS");
    private IBaseGenerator<MachineEntity> srcMachineGenerator = new QuestADMachineGenerator();
    private IBaseGenerator<MachineEntity> dstMachineGenerator = srcMachineGenerator;
    private IBaseGenerator<String>  sourceNetnameGen = new DictionaryWordGenerator(0,500);
    private IBaseGenerator<String>  destinationNetnameGen = new DictionaryWordGenerator(0,500);
    private IBaseGenerator<Location> locationGen = new AuthenticationLocationCyclicGenerator();
    private IBaseGenerator<String>  eventIdGenerator = new Md5RandomGenerator();

    public NetworkEventsGenerator() throws GeneratorException { }

    private IStringGenerator getEventIdGenerator(Entity entity) {
         return new EntityEventIDFixedPrefixGenerator(entity.MD5.get());
    }

    public NetworkEventsGenerator(ITimeGenerator timeGenerator) throws GeneratorException {
        super(timeGenerator);
    }


    @Override
    public NetworkEvent generateNext() throws GeneratorException {

        Entity entity = entityGenerator.getNext();
        Instant time = getTimeGenerator().getNext();
        String eventId = eventIdGenerator.getNext();
        String dataSource = dataSourceGenerator.getNext();
        MachineEntity srcMachine = srcMachineGenerator.getNext();
        MachineEntity dstMachine = dstMachineGenerator.getNext();
        String destinationOrganization = "";
        String destinationASN = "";
        long numOfBytesSent = 1024;
        long numOfBytesReceived = 1024;
        String sourceNetname = sourceNetnameGen.getNext();
        String destinationNetname = destinationNetnameGen.getNext();
        String ja3s = ja3Generator.getNext();
        NETWORK_DIRECTION_TYPE network_direction_type = NETWORK_DIRECTION_TYPE.OUTBOUND;
        int destinationPort = 12325;
        Location srcLocation = locationGen.getNext();
        Location dstLocation = locationGen.getNext();
        String sslCa = "";
        int sessionSplit = 0;
        boolean isSelfSigned = false;

        NetworkEvent networkEvent = new NetworkEvent(time);
        networkEvent.setEventId(eventId);
        networkEvent.setSrcMachineEntity(srcMachine);
        networkEvent.setDstMachineEntity(dstMachine);
        networkEvent.setDestinationOrganization(destinationOrganization);
        networkEvent.setDestinationASN(destinationASN);
        networkEvent.setNumOfBytesSent(numOfBytesSent);
        networkEvent.setNumOfBytesReceived(numOfBytesReceived);
        networkEvent.setSourceNetname(sourceNetname);
        networkEvent.setDestinationNetname(destinationNetname);
        networkEvent.setJa3(entity.ja3);
        networkEvent.setJa3s(ja3s);
        networkEvent.setDirection(network_direction_type);
        networkEvent.setDestinationPort(destinationPort);
        networkEvent.setDataSource(dataSource);
        networkEvent.setSrcLocation(srcLocation);
        networkEvent.setDstLocation(dstLocation);
        networkEvent.setSslSubject(entity.sslSubject);
        networkEvent.setSslCa(sslCa);
        networkEvent.setSessionSplit(sessionSplit);
        networkEvent.setIsSelfSigned(isSelfSigned);

       return networkEvent;
    }


    public void setEntityGenerator(IBaseGenerator<Entity> entityGenerator) {
        this.entityGenerator = entityGenerator;
    }

    public void setDataSourceGenerator(IBaseGenerator<String> dataSourceGenerator) {
        this.dataSourceGenerator = dataSourceGenerator;
    }

    public void setSrcMachineGenerator(IBaseGenerator<MachineEntity> srcMachineGenerator) {
        this.srcMachineGenerator = srcMachineGenerator;
    }

    public void setDstMachineGenerator(IBaseGenerator<MachineEntity> dstMachineGenerator) {
        this.dstMachineGenerator = dstMachineGenerator;
    }

    public void setSourceNetnameGen(IBaseGenerator<String> sourceNetnameGen) {
        this.sourceNetnameGen = sourceNetnameGen;
    }

    public void setDestinationNetnameGen(IBaseGenerator<String> destinationNetnameGen) {
        this.destinationNetnameGen = destinationNetnameGen;
    }

    public void setLocationGen(IBaseGenerator<Location> locationGen) {
        this.locationGen = locationGen;
    }
}
