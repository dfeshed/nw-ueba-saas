package fortscale.collection.monitoring;

import org.apache.commons.collections.CollectionUtils;
import org.kitesdk.morphline.api.Record;

/**
 * Created by shays on 03/01/2016.
 */
public class MorphlineCommandMonitoringHelper {

    public static final String ITEM_CONTEXT = "ITEM_CONTEXT";

    public void addFilteredEventToMonitoring(Record inputRecord, CollectionMessages errorMessage, String... args){
       addFilteredEventToMonitoring(inputRecord, errorMessage.getMessageId(),args);

    }

    public void addFilteredEventToMonitoring(Record inputRecord, String errorMessage, String... args){
        // Extract the event source name (usually file name), or use empty string
        // as default
        ItemContext monitoringSource=null;
        if (CollectionUtils.isNotEmpty(inputRecord.get(ITEM_CONTEXT))){
            monitoringSource =  (ItemContext)inputRecord.get(ITEM_CONTEXT).get(0);
            //If taskMonitorHelper configured - log the event. If not - ignore.
            if (monitoringSource!=null && monitoringSource.getTaskMonitoringHelper()!=null && monitoringSource.getTaskMonitoringHelper().isMonitoredTask()){
                String sourceName = monitoringSource.getSourceName();
                if (sourceName == null){
                    sourceName = "";
                }
                monitoringSource.getTaskMonitoringHelper().countNewFilteredEvents(sourceName,errorMessage,args);
            }
        }

    }

	public ItemContext getItemContext(Record inputRecord)
	{
		ItemContext result= null;
		if (CollectionUtils.isNotEmpty(inputRecord.get(ITEM_CONTEXT))){
			result = (ItemContext)inputRecord.get(ITEM_CONTEXT).get(0);
		}

		return result;

	}
}
