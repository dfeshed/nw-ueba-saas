package fortscale.ml.processes.shell;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.fixedduration.FixedDurationStrategyExecutor;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.pagination.PaginationService;
import fortscale.utils.time.TimeRange;

import java.util.List;

/**
 * Created by barak_schuster on 6/11/17.
 */
public class ScoreAggregationsService extends FixedDurationStrategyExecutor{
    PaginationService paginationService;
    RawEventsScoringService rawEventsScoringService;
    ScoreAggregationsBucketService scoreAggregationsBucketService;
    ScoreAggregationsCreator scoreAggregationsCreator;

    public ScoreAggregationsService(FixedDurationStrategy strategy) {
        super(strategy);
    }

    @Override
    public void executeSingleTimeRange(TimeRange timeRange, String dataSource) {
        List<PageIterator<Object>> pageIterators = paginationService.getPageIterators(dataSource, timeRange);
        for (PageIterator pageIterator: pageIterators) {
            while (pageIterator.hasNext())
            {
                Object next = pageIterator.next();
                rawEventsScoringService.scoreAndStore();
                scoreAggregationsBucketService.updateBuckets();
            }
            scoreAggregationsBucketService.closeBuckets();
            scoreAggregationsCreator.createScoreAggregations();

        }
    }
}
