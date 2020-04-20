package presidio.data.generators.fileentity;

import presidio.data.generators.common.ILongGenerator;

public class FileSizeIncrementalGenerator implements ILongGenerator {

    private long min = 5242880;
    private long max = 52428800;
    private long step = 524288;
    private long current;

    public FileSizeIncrementalGenerator() {
        current = min;
    }

    public FileSizeIncrementalGenerator(long min, long max, long step) {
        this.current = min;
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public long getNext(){
        long ret = current;
        current = (ret + step > max) ? min : current+step;
        return ret;
    }
}
