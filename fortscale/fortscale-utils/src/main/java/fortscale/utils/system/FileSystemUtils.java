package fortscale.utils.system;

import java.io.File;

/**
 * Created by galiar on 09/05/2016.
 */
public class FileSystemUtils {

    /**
     * return the free space of the entire disk where the file reside, rather than the subtree beneath the file
     * e.g. the results when filename == D: and when filename == D:\subDirectory\verySpecificFile will be identical,
     * since they both reside in D: file system
     * the results would be different (probably) to rootDir = D: and rootDir == C:
     * @param filename the file
     * @return free space in disk where the file is located
     */
    public long getFreeSpace(String filename){
        return new File(filename).getFreeSpace();
    }

    /**
     * return the total space of the entire disk where the file reside, rather than the subtree beneath the file
     * for example see getFreeSpace above
     * @param filename the file
     * @return total space in disk where the file is located
     */
    public long getTotalSpace(String filename){
        return new File(filename).getTotalSpace();
    }

}
