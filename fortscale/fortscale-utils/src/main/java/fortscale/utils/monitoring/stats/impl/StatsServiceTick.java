package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsService;

/**
 * Created by gaashh on 5/22/16.
 */
public class StatsServiceTick implements Runnable {

    protected final StatsService statsService;

    StatsServiceTick (StatsService statsService) {
        this.statsService = statsService;
    }

    public void run() {
        long epoch = System.currentTimeMillis() / 1000;
        statsService.tick(epoch);
    }
}
