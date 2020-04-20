package presidio.data.generators.user;

import org.springframework.util.Assert;
import presidio.data.domain.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NumberedUserRandomUniformallyGenerator implements IUserGenerator{
    private static final String[] numNames = {
            "Zero",
            "One",
            "Two",
            "Three",
            "Four",
            "Five",
            "Six",
            "Seven",
            "Eight",
            "Nine"
    };
    private int numOfUsers;
    private int startNumber;
    private String namePrefix;
    private String idPrefix;
    private boolean isAdmin;
    private Random random;
    private boolean isUserNameContainNumbers;
    private Map<Integer, String> randIndexToUserNameMap = new HashMap<>();

    public NumberedUserRandomUniformallyGenerator(int numOfUsers, int startNumber, String namePrefix, String idPrefix, boolean isAdmin){
        this(numOfUsers, startNumber, namePrefix, idPrefix,isAdmin, true);
    }
    public NumberedUserRandomUniformallyGenerator(int numOfUsers, int startNumber, String namePrefix, String idPrefix,
                                                  boolean isAdmin, boolean isUserNameContainNumbers){
        Assert.isTrue(numOfUsers>0, "numOfUsers should be bigger positive");
        Assert.isTrue(startNumber>=0, "startNumber should be >= 0");
        Assert.notNull(namePrefix, "namePrefix should not be null");
        Assert.notNull(idPrefix, "idPrefix should not be null");

        this.numOfUsers = numOfUsers;
        this.startNumber = startNumber;
        this.namePrefix = namePrefix;
        this.idPrefix = idPrefix;
        this.isAdmin = isAdmin;
        random = new Random(0);
        this.isUserNameContainNumbers = isUserNameContainNumbers;
    }


    @Override
    public User getNext() {
        int randNumber = random.nextInt(numOfUsers);
        String userName;
        String userId;
        if(isUserNameContainNumbers) {
            userName = namePrefix + randNumber;
            userId = idPrefix + randNumber;
        } else{
            userName = getUserName(randNumber);
            userId = idPrefix + userName;
        }
        User user = new User(userName, userId, userName + "FirstName", userName + "LastName", isAdmin);

        return user;
    }

    private String getUserName(int randNumber){
        return randIndexToUserNameMap.computeIfAbsent(randNumber, k -> createUsername(randNumber));
    }

    private String createUsername(int randNumber){
        StringBuilder builder = new StringBuilder();
        builder.append(namePrefix);
        String randNumberStr = Integer.toString(randNumber);
        for(String number: randNumberStr.split("")){
            int i = Integer.parseInt(number);
            builder.append("_").append(numNames[i]);
        }
        return builder.toString();
    }

    @Override
    public Long getMaxNumOfDistinctUsers(){
        return Long.valueOf(numOfUsers);
    }
}
