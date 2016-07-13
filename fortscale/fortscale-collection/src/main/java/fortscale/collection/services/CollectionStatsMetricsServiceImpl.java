package fortscale.collection.services;

import fortscale.collection.metrics.ETLCommonJobMetircs;
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

	private HashMap<String,MorphlineMetrics> morphlinesMetrics;

	private HashMap<String,ETLCommonJobMetircs> commonJobMetircs;

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

	public ETLCommonJobMetircs getETLCommonJobMetircs(String dataSource){

		//validate if the specific data source ETLCommonMetrics metric exist on the Map
		//If not create it, insert it to the map and return the new instance
		ETLCommonJobMetircs result = commonJobMetircs.get(dataSource);

		if(result==null)
		{
			result = new ETLCommonJobMetircs(statsService,dataSource);
			commonJobMetircs.put(dataSource,result);

		}

		return result;
	}

}
