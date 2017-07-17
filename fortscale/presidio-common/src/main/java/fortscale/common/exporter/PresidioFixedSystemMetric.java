package fortscale.common.exporter;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
class PresidioFixedSystemMetric {

    private final static String MEM="mem";
    private final static String MEM_FREE="mem.free";
    private final static String PROCESSORS="processors";
    private final static String UPTIME="uptime";
    private final static String SYSTEMLOAD_AVERAGE="systemload.average";
    private final static String HEAP_COMMITTED="heap.committed";
    private final static String HEAP_INIT="heap.init";
    private final static String HEAP_USED="heap.used";
    private final static String HEAP="heap";
    private final static String NONHEAP_COMMITTED="nonheap.committed";
    private final static String NONHEAP_INIT="nonheap.init";
    private final static String NONHEAP_USED="nonheap.used";
    private final static String NONHEAP="nonheap";
    private final static String THREADS_PEAK="threads.peak";
    private final static String THREADS_DAEMON="threads.daemon";
    private final static String THREADS_TOTAL_STARTED="threads.totalStarted";
    private final static String THREADS="threads";
    private final static String GC_PS_SCAVENGE_COUNT="gc.ps_scavenge.count";
    private final static String GC_PS_SCAVENGE_TIME="gc.ps_scavenge.time";
    private final static String GC_PS_MARKSWEEP_COUNT="gc.ps_marksweep.count";
    private final static String GC_PS_MARKSWEEP_TIME="gc.ps_marksweep.time";

    static Set<String> listOfPresidioFixedSystemMetric(){
        return new HashSet<>(Arrays.asList(MEM, MEM_FREE,PROCESSORS,UPTIME,SYSTEMLOAD_AVERAGE,HEAP_COMMITTED,
                HEAP_INIT,HEAP_USED,HEAP,NONHEAP_COMMITTED,NONHEAP_INIT,NONHEAP,NONHEAP_USED,THREADS_PEAK,THREADS_DAEMON,
                THREADS_TOTAL_STARTED,THREADS,GC_PS_SCAVENGE_COUNT,GC_PS_SCAVENGE_TIME,GC_PS_MARKSWEEP_COUNT,GC_PS_MARKSWEEP_TIME));
    }

}
