package presidio.data.generators.machine;

import presidio.data.domain.MachineEntity;
import presidio.data.domain.User;

import java.util.*;



public class UserServerGenerator implements IMachineGenerator {
    public static User user = null;

    int minNumOfServersPerUser;
    int maxNumOfServersPerUser;
    Map<String, List<MachineEntity>> userIdToMachineEntityListMap = new HashMap<>();
    List<MachineEntity> machineEntitiesPool;
    Random random;

    public UserServerGenerator(List<MachineEntity> machineEntitiesPool,
                                   int minNumOfServersPerUser, int maxNumOfServersPerUser){
        this.machineEntitiesPool = machineEntitiesPool;
        this.minNumOfServersPerUser = minNumOfServersPerUser;
        this.maxNumOfServersPerUser = maxNumOfServersPerUser;
        random = new Random(0);
    }

    @Override
    public MachineEntity getNext() {
        List<MachineEntity> userMachineEntities = userIdToMachineEntityListMap.computeIfAbsent(getUserId(), k -> createUserMachineEntities());
        int randNumber = random.nextInt(userMachineEntities.size());

        return userMachineEntities.get(randNumber);
    }

    private List<MachineEntity> createUserMachineEntities(){
        List<MachineEntity> ret = new ArrayList<>();
        int numOfMachineEntities = random.nextInt(maxNumOfServersPerUser - minNumOfServersPerUser) + minNumOfServersPerUser;
        for(int i = 0; i < numOfMachineEntities; i++){
            int randNumber = random.nextInt(machineEntitiesPool.size());
            ret.add(machineEntitiesPool.get(randNumber));
        }
        return ret;
    }

    private String getUserId(){
        return user.getUserId();
    }
}
