package fortscale.collection.morphlines.metrics;

/**
 * Created by idanp on 6/27/2016.
 */

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for Morphlines
 *
 */
@StatsMetricsGroupParams(name = "ETL.Morphlines")
public class MorphlineMetrics extends StatsMetricsGroup {

	public MorphlineMetrics(StatsService statsService,String dataSource) {
		// Call parent ctor
		super(statsService, MorphlineMetrics.class,
				// Create anonymous attribute class with initializer block since it does not have ctor
				new StatsMetricsGroupAttributes() {
					{
						addTag("data-source", dataSource);
					}
				}
		);

	}


}
