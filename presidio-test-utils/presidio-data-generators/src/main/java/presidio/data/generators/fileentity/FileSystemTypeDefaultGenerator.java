package presidio.data.generators.fileentity;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class FileSystemTypeDefaultGenerator extends CyclicValuesGenerator<String> implements IStringGenerator {

    private static final String[] DEFAULT_EXTENSIONS = {"1"};

    public FileSystemTypeDefaultGenerator() {
        super((DEFAULT_EXTENSIONS));
    }

    public FileSystemTypeDefaultGenerator(String[] exts) {
        super((exts));
    }
}
