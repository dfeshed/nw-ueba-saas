package fortscale.collection.morphlines.metrics;

/**
 * Created by idanp on 6/27/2016.
 */

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for Morphlines
 *
 */
@StatsMetricsGroupParams(name = "ETL.morphlines")
public class MorphlineMetrics extends StatsMetricsGroup {

	public MorphlineMetrics(StatsService statsService,String dataSource) {
		// Call parent ctor
		super(statsService, MorphlineMetrics.class,
				// Create anonymous attribute class with initializer block since it does not have ctor
				new StatsMetricsGroupAttributes() {
					{
						addTag("dataSource", dataSource);
					}
				}
		);

	}

	// Number of process() task function calls.
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long filteredDirectedFromMorphline;

	// Number of records with empty value at time field.
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long emptyTimeField;

	// Number of records with unvalid value at time field.
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unvalidTimeField;

	//Number of records that year was added successfully
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long addYearToDatetimeSuccess;


	// Number of erros in writing to computerLogin repo
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long errorsInWritingToComputerLogins;

	//Number of unparseable timestamps
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long unparseableTimeStamps;

	//Number of filtered record due to some missing value
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long recordMissingValue;

	//Number of filtered record due to some missing value
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long recordMissingSpecificValueForSpecificField;

	//Number of records that was saved to the cache
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long recordsThatWasSavedToEventJoinrCache;

	//Number of records where the computer login resolver was null
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginResolverNull;

	//Number of records that updated the computer login evemts
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerLoginUpdatedSuccessfully;

	//Number of matches found in contains command
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long containsCommandFoundMatch;

	//Number of records without matches
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long containsCommandDidntFindMatch;

	//Number of eventsSavedToCache
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long eventSavedToCache;

	//Number of records that had delta greater than threshold
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long deltaGreaterThenThreshold;

	//Number of events dropped
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long eventDropped;

	//Number of events joined
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long eventJoinerStore;

	//Number of records that had not computer as account name
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long accountNameNoComputer;

	//Number of records that had computer as account name
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long accountNameComputer;

	//Number of records where the service name didn't match regular expression
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long serviceNameNotMatchRegularExpression;

	//Number of records that had not computer as service name
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long serviceNameIsNotComputer;

	//Number of records that had computer as service name
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long serviceNameIsComputer;
}
