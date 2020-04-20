package fortscale.common.general;

import fortscale.utils.time.TimeRange;

public class ThreadLocalWithBatchInformation {

    private static ThreadLocal<TimeRange> currentProcessedTime = new ThreadLocal<>();


    public static TimeRange getCurrentProcessedTime() {
        return currentProcessedTime.get();
    }

    public static String getProcessName() {
        return  Thread.currentThread().getName();
    }

    public static void storeBatchInformation(String batchName, TimeRange processedTime) {
        currentProcessedTime.set(processedTime);
        Thread.currentThread().setName(batchName);
    }
}
