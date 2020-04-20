package presidio.data.generators.common;

import presidio.data.domain.User;
import presidio.data.domain.event.OperationType;

import java.util.*;

public class UserOperationTypeGenerator implements IOperationTypeGenerator {

    public static User user = null;

    int minNumOfOperationTypesPerUser;
    int maxNumOfOperationTypesPerUser;
    Map<String, List<OperationType>> userIdToOperationTypeListMap = new HashMap<>();
    List<OperationType> operationTypesPool;
    Random random;

    public UserOperationTypeGenerator(List<OperationType> operationTypesPool,
                                      int minNumOfOperationTypesPerUser, int maxNumOfOperationTypesPerUser){
        this.operationTypesPool = operationTypesPool;
        this.minNumOfOperationTypesPerUser = minNumOfOperationTypesPerUser;
        this.maxNumOfOperationTypesPerUser = maxNumOfOperationTypesPerUser;
        random = new Random(0);
    }

    @Override
    public OperationType getNext() {
        List<OperationType> userOperationTypes = userIdToOperationTypeListMap.computeIfAbsent(getUserId(), k -> createUserOperationTypes());
        int randNumber = random.nextInt(userOperationTypes.size());

        return userOperationTypes.get(randNumber);
    }

    private List<OperationType> createUserOperationTypes(){
        List<OperationType> ret = new ArrayList<>();
        int numOfOperationTypes = random.nextInt(maxNumOfOperationTypesPerUser - minNumOfOperationTypesPerUser) + minNumOfOperationTypesPerUser;
        for(int i = 0; i < numOfOperationTypes; i++){
            int randNumber = random.nextInt(operationTypesPool.size());
            ret.add(operationTypesPool.get(randNumber));
        }
        return ret;
    }

    private String getUserId(){
        return user.getUserId();
    }
}
