package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.generators.entity.IBaseGenerator;

public interface IMachineGenerator extends IBaseGenerator<MachineEntity> {
    MachineEntity getNext();
}
