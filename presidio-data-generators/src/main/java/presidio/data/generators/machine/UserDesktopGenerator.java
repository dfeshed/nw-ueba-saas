package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;

public class UserDesktopGenerator implements IMachineGenerator{
    public static User user;

    @Override
    public MachineEntity getNext() {
        String machineId = user.getUserId() + "_PC";
        return new MachineEntity(
                machineId,
                "",
                machineId,
                "",
                "",
                "",
                "",
                user.getUserId());
    }
}
