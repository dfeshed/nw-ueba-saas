package presidio.data.generators.file;

import presidio.data.generators.common.IStringGenerator;

import java.io.File;

public class AbsoluteFilePathGenerator implements IStringGenerator {

    private SimplePathGenerator pathGenerator = new SimplePathGenerator();
    private FileNameDefaultExtGenerator fileNameGenerator = new FileNameDefaultExtGenerator();

    public AbsoluteFilePathGenerator() {
        super();
    }

    public String getNext(){
        return pathGenerator.getNext() + File.pathSeparator + fileNameGenerator.getNext();
    }

}
