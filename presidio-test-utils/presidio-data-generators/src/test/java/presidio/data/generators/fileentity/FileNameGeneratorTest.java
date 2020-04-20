package presidio.data.generators.fileentity;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by cloudera on 6/1/17.
 */
public class FileNameGeneratorTest {

   /***
     * File name is built as concatenation of "File"
     * and extension taken from list of default extensions specified in the generator itself.
     * Default extensions:
     * "jar","db","_db","mdb","accdb","wdb","sqlite","sdf","myd","dbf",
     * "db3","sql","dbs","mdf","cpp","7z","gzip","zip","rar","tar",
     * "war","dmg","taz","tbz","tbz2"
     */
    @Test
    public void FileNameGeneratorTest() {
        String expected1 = "File.jar";
        String expected25 = "File.tbz2";

        FileNameDefaultExtGenerator FNG = new FileNameDefaultExtGenerator();

        // check first extension
        Assert.assertEquals(expected1, FNG.getNext());

        // skip 2-24 extensions
        for (int i = 1; i < 24; i++) FNG.getNext();

        // check last (25th) extension
        Assert.assertEquals(expected25, FNG.getNext());
    }

    /***
     * Test file names generated with custom extensions
     */
    @Test
    public void FileNameGenerator1Test() {
        String[] myExtensions = {"doc", "pdf", "mkb"};
        FileNameDefaultExtGenerator FNG = new FileNameDefaultExtGenerator(myExtensions);

        Assert.assertEquals("File.doc", FNG.getNext());
        Assert.assertEquals("File.pdf", FNG.getNext());
        Assert.assertEquals("File.mkb", FNG.getNext());
    }

}
