package presidio.data.generators.fileentity;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.generators.common.IStringGenerator;

public class FileSystemTypeDefaultGenerator extends AbstractCyclicValuesGenerator implements IStringGenerator {

    private static final String[] DEFAULT_EXTENSIONS = {"1"};

    public FileSystemTypeDefaultGenerator() {
        super((DEFAULT_EXTENSIONS));
    }

    public FileSystemTypeDefaultGenerator(String[] exts) {
        super((exts));
    }
}
