package presidio.data.generators.fileentity;

import presidio.data.domain.FileEntity;
import presidio.data.domain.User;
import presidio.data.generators.fileentity.IFileEntityGenerator;

import java.util.*;

public class UserFileEntityGenerator implements IFileEntityGenerator {
    public static User user = null;

    int minNumOfFilesPerUser;
    int maxNumOfFilesPerUser;
    Map<String, List<FileEntity>> userIdToFileEntityListMap = new HashMap<>();
    List<FileEntity> fileEntitiesPool;
    Random random;

    public UserFileEntityGenerator(List<FileEntity> fileEntitiesPool,
                                   int minNumOfFilesPerUser, int maxNumOfFilesPerUser){
        this.fileEntitiesPool = fileEntitiesPool;
        this.minNumOfFilesPerUser = minNumOfFilesPerUser;
        this.maxNumOfFilesPerUser = maxNumOfFilesPerUser;
        random = new Random();
    }

    @Override
    public FileEntity getNext() {
        List<FileEntity> userFileEntities = userIdToFileEntityListMap.computeIfAbsent(getUserId(), k -> createUserFileEntities());
        int randNumber = random.nextInt(userFileEntities.size());

        return userFileEntities.get(randNumber);
    }

    private List<FileEntity> createUserFileEntities(){
        List<FileEntity> ret = new ArrayList<>();
        int numOfFileEntities = random.nextInt(maxNumOfFilesPerUser - minNumOfFilesPerUser) + minNumOfFilesPerUser;
        for(int i = 0; i < numOfFileEntities; i++){
            int randNumber = random.nextInt(fileEntitiesPool.size());
            ret.add(fileEntitiesPool.get(randNumber));
        }
        return ret;
    }

    private String getUserId(){
        return user.getUserId();
    }
}
