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

	//Number of records that extracted domain name
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long extractingDomainName;

	//Number of records that had time zone null
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long timeZoneNull;

	//Number of records that written to hdfs
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long writtenToHdfs;

	//Number of records had errors writing to hdfs
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long errorWritingToHdfs;

	//Number of records that we didn't recognized the compute name in AD
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerServiceNotInAD;

	//Number of records that we recognized the compute name in AD
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long computerServiceFoundInAD;

	//Number of records that were filtered
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long logFilteredEvent;

	//Number of records that we couldn't find a match for the ip
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long ipNotMatched;

	//Number of records that we found match for ip
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long ipMatched;

	//Number of records that we could not match hostname to regex
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long couldNotMatchHostnameToRegex;

	//Number of records that the hostname was in filter list
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long hostnameInFilterList;

	//Number of records that dropped because of overflow the threshold
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long overflowThresholdReached;

	//Number of records that parsed field
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long parsingField;

	//Number of records with error converting the kerberos ticket
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long errorConvertingKerberosTicket;

	//Number of records we converted kerberos ticket
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long kerberosTicketConverted;

	//Number of records that were filtered on field match
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long regexMatchFilteredOnFieldMatched;

	//Number of records that were filtered on field that didn't match
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long regexMatchFilteredOnFieldNotMatched;

	//Number of records that weren't filtered on regex match
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long regexMatchRecordNotFiltered;

	//Number of records that sent notification
	@StatsDoubleMetricParams(rateSeconds = 1)
	public long sendNotification;
}
