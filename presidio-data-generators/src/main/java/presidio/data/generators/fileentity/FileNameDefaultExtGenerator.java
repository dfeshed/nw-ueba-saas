package presidio.data.generators.fileentity;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class FileNameDefaultExtGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {

    private static final String[] DEFAULT_EXTENSIONS = {"jar","db","_db","mdb","accdb","wdb","sqlite","sdf","myd",
            "dbf","db3","sql","dbs","mdf","cpp","7z","gzip","zip","rar","tar","war","dmg","taz","tbz","tbz2"};

    public FileNameDefaultExtGenerator() {
        super(buildValues(DEFAULT_EXTENSIONS));
    }

    public FileNameDefaultExtGenerator(String[] exts) {
        super(buildValues(exts));
    }

    private static String[] buildValues(String[] extensions){
        String[] fileNames = new String[extensions.length];
        for ( int i = 0; i < extensions.length; i++)
        {
            fileNames[i] = "File." + extensions[i];
        }
        return fileNames;
    }
}
