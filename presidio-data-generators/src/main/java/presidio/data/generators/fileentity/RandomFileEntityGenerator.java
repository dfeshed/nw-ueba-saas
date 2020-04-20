package presidio.data.generators.fileentity;

import presidio.data.domain.FileEntity;

import java.util.Random;

public class RandomFileEntityGenerator implements IFileEntityGenerator{

    private int numOfDirectories;
    private String directoryPrefix;
    private String directorySuffix;
    private int numOfFiles;
    private String filePrefix;
    private String fileSuffix;
    private Random random;

    public RandomFileEntityGenerator(int numOfDirectories, String directoryPrefix, String directorySuffix,
                                     int numOfFiles, String filePrefix, String fileSuffix){
        this.numOfDirectories = numOfDirectories;
        this.directoryPrefix = directoryPrefix;
        this.directorySuffix = directorySuffix;
        this.numOfFiles = numOfFiles;
        this.filePrefix = filePrefix;
        this.fileSuffix = fileSuffix;
        random = new Random(0);
    }

    @Override
    public FileEntity getNext() {
        String directory = directoryPrefix + random.nextInt(numOfDirectories) + directorySuffix;
        String file = filePrefix + random.nextInt(numOfFiles) + fileSuffix;
        return new FileEntity(file, directory, 100L, false, false);
    }
}
