package presidio.data.generators.event.performance;

import org.apache.commons.lang3.tuple.Pair;
import presidio.data.domain.FileEntity;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.fileentity.ProcessFileEntityGenerator;
import presidio.data.generators.machine.IMachineGenerator;
import presidio.data.generators.user.IUserGenerator;

import java.util.ArrayList;
import java.util.List;


public abstract class EndpointEventGeneratorsBuilder extends UserOrientedEventGeneratorsBuilder {

    /** MACHINES **/
    protected IMachineGenerator machineGenerator;

    /** Processes **/
    protected  List<FileEntity> nonImportantProcesses;


    public EndpointEventGeneratorsBuilder(IUserGenerator normalUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> normalUserAbnormalActivityRange,
                                         IUserGenerator adminUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserActivityRange,
                                         List<MultiRangeTimeGenerator.ActivityRange> adminUserAbnormalActivityRange,
                                         IUserGenerator serviceAccountUserGenerator,
                                         List<MultiRangeTimeGenerator.ActivityRange> serviceAcountUserActivityRange,
                                         IMachineGenerator machineGenerator,
                                         List<FileEntity> nonImportantProcesses){
        super(normalUserGenerator,
                normalUserActivityRange,
                normalUserAbnormalActivityRange,
                adminUserGenerator,
                adminUserActivityRange,
                adminUserAbnormalActivityRange,
                serviceAccountUserGenerator,
                serviceAcountUserActivityRange);
        this.machineGenerator = machineGenerator;
        this.nonImportantProcesses = nonImportantProcesses;
    }

    protected List<FileEntity> getFileEnities(Pair[] dirAndFilePair){
        List<FileEntity> ret = new ArrayList<>();
        ProcessFileEntityGenerator fileGenerator = new ProcessFileEntityGenerator(dirAndFilePair);
        for(int i = 0; i < dirAndFilePair.length; i++){
            ret.add(fileGenerator.getNext());
        }
        return ret;
    }
}
