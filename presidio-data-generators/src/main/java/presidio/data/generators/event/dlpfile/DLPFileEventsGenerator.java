package presidio.data.generators.event.dlpfile;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.precentage.BooleanPercentageGenerator;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;
import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;
import presidio.data.domain.event.dlpfile.DLPFileEvent;
import presidio.data.domain.event.dlpfile.DLPFileOperation;
import presidio.data.generators.event.EntityEventIDFixedPrefixGenerator;
import presidio.data.generators.event.IEventGenerator;
import presidio.data.generators.dlpfileop.DLPFileOperationGenerator;
import presidio.data.generators.dlpfileop.IDLPFileOperationGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.machine.QuestADMachineGenerator;
import presidio.data.generators.user.IUserGenerator;
import presidio.data.generators.user.RandomUserGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DLPFileEventsGenerator implements IEventGenerator {
    // DEFINE ALL ATTRIBUTE GENERATORS
    private ITimeGenerator timeGenerator;

    private IUserGenerator userGenerator;
    private IMachineGenerator sourceMachineGenerator;
    private IDLPFileOperationGenerator fileOperationGenerator; // Handles: source_path, destination_path, source_file_name, destination_file_name, file_size

    private IStringGenerator eventIDGen;
    private ExecutingApplicationCyclicGenerator executingApplicationGenerator;

    private DriveTypePercentageGenerator sourceDriveTypeGenerator;
    private DriveTypePercentageGenerator destinationDriveTypeGenerator;

    private BooleanPercentageGenerator wasClassifiedGenerator;
    private BooleanPercentageGenerator wasBlockedGenerator;
    private SimpleMalwareScanResultGenerator malwareScanResultGenerator;

    public DLPFileEventsGenerator() throws GeneratorException {
        timeGenerator = new MinutesIncrementTimeGenerator();

        userGenerator = new RandomUserGenerator();
        User user = userGenerator.getNext();

        sourceMachineGenerator = new QuestADMachineGenerator();
        fileOperationGenerator = new DLPFileOperationGenerator(); // Handles: source_path, destination_path, source_file_name, destination_file_name, file_size

        eventIDGen = new EntityEventIDFixedPrefixGenerator(user.getUsername());
        executingApplicationGenerator = new ExecutingApplicationCyclicGenerator();

        sourceDriveTypeGenerator = new DriveTypePercentageGenerator();
        destinationDriveTypeGenerator = new DriveTypePercentageGenerator();

        wasClassifiedGenerator = new BooleanPercentageGenerator();
        wasBlockedGenerator = new BooleanPercentageGenerator();
        malwareScanResultGenerator = new SimpleMalwareScanResultGenerator();
    }


    public List<DLPFileEvent> generate () throws GeneratorException {
        List<DLPFileEvent> evList = new ArrayList<>() ;

        // fill list of events
        while (getTimeGenerator().hasNext()) {
            Instant currentTime = getTimeGenerator().getNext();
            User user = getUserGenerator().getNext();

            DLPFileEvent ev = new DLPFileEvent(currentTime, user.getUsername());

            ev.setNormalizedUsername(user.getUserId());
            ev.setEventId((String)getEventIDGen().getNext());
            ev.setFirstName(user.getFirstName());
            ev.setLastName(user.getLastName());

            MachineEntity sm = getSourceMachineGenerator().getNext();
            //ev.setSrcMachine(sm.getName());
            //ev.setNormalized_src_machine(sm.getNormalized_name());
            //ev.setSourceIp(sm.getIp_address());
            ev.setExecutingApplication((String)getExecutingApplicationGenerator().getNext());

            DLPFileOperation f = getFileOperationGenerator().getNext();
            ev.setEventType(f.getEvent_type());
            ev.setSourcePath(f.getSource_path());
            ev.setDestinationPath(f.getDestination_path());
            ev.setSourceFileName(f.getSource_file_name());
            ev.setDestinationFileName(f.getDestination_file_name());
            ev.setFileSize(f.getFile_size());

            ev.setSourceDriveType((String)getSourceDriveTypeGenerator().getNext());
            ev.setDestinationDriveType((String)getDestinationDriveTypeGenerator().getNext());
            ev.setWasClassified(getWasClassifiedGenerator().getNext());
            ev.setWasBlocked(getWasBlockedGenerator().getNext());
            ev.setMalwareScanResult((String)getMalwareScanResultGenerator().getNext());
            evList.add(ev);
        }
        return evList;
    }

    public void setTimeGenerator(ITimeGenerator timeGenerator) {
        this.timeGenerator = timeGenerator;
    }

    public void setUserGenerator(IUserGenerator userGenerator) throws GeneratorException {
        this.userGenerator = userGenerator;
    }

    public void setExecutingApplicationListGenerator(ExecutingApplicationCyclicGenerator executingApplicationListGenerator) {
        this.executingApplicationGenerator = executingApplicationListGenerator;
    }

    public void setSourceMachineGenerator(IMachineGenerator sourceMachineGenerator) throws GeneratorException {
        this.sourceMachineGenerator = sourceMachineGenerator;
    }

    public ITimeGenerator getTimeGenerator() {
        return timeGenerator;
    }

    public IUserGenerator getUserGenerator() {
        return userGenerator;
    }

    public IMachineGenerator getSourceMachineGenerator() {
        return sourceMachineGenerator;
    }

    public IDLPFileOperationGenerator getFileOperationGenerator() {
        return fileOperationGenerator;
    }

    public IStringGenerator getEventIDGen() {
        return eventIDGen;
    }

    public ExecutingApplicationCyclicGenerator getExecutingApplicationGenerator() {
        return executingApplicationGenerator;
    }

    public DriveTypePercentageGenerator getSourceDriveTypeGenerator() {
        return sourceDriveTypeGenerator;
    }

    public DriveTypePercentageGenerator getDestinationDriveTypeGenerator() {
        return destinationDriveTypeGenerator;
    }

    public BooleanPercentageGenerator getWasClassifiedGenerator() {
        return wasClassifiedGenerator;
    }

    public BooleanPercentageGenerator getWasBlockedGenerator() {
        return wasBlockedGenerator;
    }

    public SimpleMalwareScanResultGenerator getMalwareScanResultGenerator() {
        return malwareScanResultGenerator;
    }

    public void setWasBlockedGenerator(BooleanPercentageGenerator wasBlockedGenerator) {
        this.wasBlockedGenerator = wasBlockedGenerator;
    }

    public void setWasClassifiedGenerator(BooleanPercentageGenerator wasClassifiedGenerator) {
        this.wasClassifiedGenerator = wasClassifiedGenerator;
    }

    public void setSourceDriveTypeGenerator(DriveTypePercentageGenerator driveTypeGenerator) {
        this.sourceDriveTypeGenerator = driveTypeGenerator;
    }
    public void setDestDriveTypeGenerator(DriveTypePercentageGenerator driveTypeGenerator) {
        this.destinationDriveTypeGenerator = driveTypeGenerator;
    }

    public void setEventIDGenerator(IStringGenerator eventIDGen) throws GeneratorException {
        this.eventIDGen = eventIDGen;
    }

    public void setFileOperationGenerator(IDLPFileOperationGenerator fileOperationGenerator) {
        this.fileOperationGenerator = fileOperationGenerator;
    }

    public void setEventIDGen(EntityEventIDFixedPrefixGenerator eventIDGen) {
        this.eventIDGen = eventIDGen;
    }

    public void setExecutingApplicationGenerator(ExecutingApplicationCyclicGenerator executingApplicationGenerator) {
        this.executingApplicationGenerator = executingApplicationGenerator;
    }

    public void setDestinationDriveTypeGenerator(DriveTypePercentageGenerator destinationDriveTypeGenerator) {
        this.destinationDriveTypeGenerator = destinationDriveTypeGenerator;
    }

    public void setMalwareScanResultGenerator(SimpleMalwareScanResultGenerator malwareScanResultGenerator) {
        this.malwareScanResultGenerator = malwareScanResultGenerator;
    }
}
