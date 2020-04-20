package presidio.data.generators.user;

import presidio.data.domain.User;

import java.util.*;

public class LimitNumOfUsersGenerator implements IUserGenerator{

    private int numOfUsersLimit;
    private IUserGenerator userGenerator;
    private Map<String, User> userIdToUser = new HashMap<>();
    private List<User> users = new ArrayList<>();
    Random rand;

    public LimitNumOfUsersGenerator(int numOfUsersLimit, IUserGenerator userGenerator){
        this.numOfUsersLimit = numOfUsersLimit;
        this.userGenerator = userGenerator;
        rand = new Random(0);
    }

    @Override
    public User getNext() {
        User ret = null;
        if(users.size()>= numOfUsersLimit){
            int randNumber = rand.nextInt(numOfUsersLimit);
            ret = users.get(randNumber);
        } else{
            ret = userGenerator.getNext();
            if(userIdToUser.put(ret.getUserId(), ret) == null){
                users.add(ret);
            }
            if(users.size() == numOfUsersLimit){
                userIdToUser = null;
            }
        }
        return ret;
    }

    @Override
    public Long getMaxNumOfDistinctUsers(){
        return Long.valueOf(numOfUsersLimit);
    }
}
