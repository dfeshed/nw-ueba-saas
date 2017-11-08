package presidio.data.generators.user;

import org.springframework.util.Assert;
import presidio.data.domain.User;

import java.util.Random;

public class NumberedUserRandomUniformallyGenerator implements IUserGenerator{
    private int numOfUsers;
    private int startNumber;
    private String namePrefix;
    private String idPrefix;
    private boolean isAdmin;
    private Random random;

    public NumberedUserRandomUniformallyGenerator(int numOfUsers, int startNumber, String namePrefix, String idPrefix, boolean isAdmin){
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
    }


    @Override
    public User getNext() {
        int randNumber = random.nextInt(numOfUsers);
        String userName = namePrefix + randNumber;
        User user = new User(userName, idPrefix + randNumber, userName + "FirstName", userName + "LastName", isAdmin);

        return user;
    }
}
