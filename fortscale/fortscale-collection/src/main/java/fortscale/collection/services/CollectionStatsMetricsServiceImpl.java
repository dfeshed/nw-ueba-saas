package fortscale.collection.services;

import fortscale.collection.metrics.ETLCommonJobMetrics;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.utils.monitoring.stats.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by idanp on 6/27/2016.
 */
@Service("collectionStatsMetricsServiceImpl")
public class CollectionStatsMetricsServiceImpl implements CollectionStatsMetricsService {

	@Autowired
	private StatsService statsService;

	private HashMap<String,MorphlineMetrics> morphlinesMetrics = new HashMap<>();

	private HashMap<String,ETLCommonJobMetrics> commonJobMetrics = new HashMap<>();

	public MorphlineMetrics getMorphlineMetrics(String dataSource)
	{
		//validate if the specific data source morphline metric exist on the Map
		//If not create it, insert it to the map and return the new instance
		MorphlineMetrics result = morphlinesMetrics.get(dataSource);

		if(result==null)
		{
			result = new MorphlineMetrics(statsService,dataSource);
			morphlinesMetrics.put(dataSource,result);

		}

		return result;
	}

	public ETLCommonJobMetrics getETLCommonJobMetrics(String dataSource){

		//validate if the specific data source ETLCommonMetrics metric exist on the Map
		//If not create it, insert it to the map and return the new instance
		ETLCommonJobMetrics result = commonJobMetrics.get(dataSource);

		if(result==null)
		{
			result = new ETLCommonJobMetrics(statsService,dataSource);
			commonJobMetrics.put(dataSource,result);

		}

		return result;
	}

}
