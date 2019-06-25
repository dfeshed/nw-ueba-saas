package presidio.data.generators.fileop;

import presidio.data.domain.User;
import presidio.data.domain.event.OperationType;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.*;

public class UserFileOperationTypeGenerator implements IOperationTypeGenerator {

    public static User user = null;

    int minNumOfOperationTypesPerUser;
    int maxNumOfOperationTypesPerUser;
    Map<String, List<OperationType>> userIdToOperationTypeListMap = new HashMap<>();
    List<OperationType> operationTypesPool;
    Random random;

    public UserFileOperationTypeGenerator(List<OperationType> operationTypesPool,
                                   int minNumOfOperationTypesPerUser, int maxNumOfOperationTypesPerUser){
        this.operationTypesPool = operationTypesPool;
        this.minNumOfOperationTypesPerUser = minNumOfOperationTypesPerUser;
        this.maxNumOfOperationTypesPerUser = maxNumOfOperationTypesPerUser;
        random = new Random();
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
