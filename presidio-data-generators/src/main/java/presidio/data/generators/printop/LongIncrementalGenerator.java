package presidio.data.generators.printop;

import presidio.data.generators.common.ILongGenerator;

public class LongIncrementalGenerator implements ILongGenerator {

    private long min = 1;
    private long max = 10000000;
    private long step = 15;
    private long current;

    public LongIncrementalGenerator() {
        current = min;
    }

    public LongIncrementalGenerator(long min, long max, long step) {
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
