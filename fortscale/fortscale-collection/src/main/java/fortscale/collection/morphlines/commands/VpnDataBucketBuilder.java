package fortscale.collection.morphlines.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;

import com.typesafe.config.Config;

import fortscale.collection.morphlines.RecordExtensions;



@Configurable(preConstruction=true)
public class VpnDataBucketBuilder implements CommandBuilder {
	
	private static final long DURATION_BUCKET_IN_SEC = 60*60;
	private static final int MEGA_BYTE = 1000000;
	
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("VpnDataBucket");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new VpnDataBucket(this, config, parent, child, context);
	}

	
	private class VpnDataBucket extends AbstractCommand {

		private String totalbytesFieldName;
		private String readbytesFieldName;
		private String durationFieldName;
		private String databucketFieldName;
		private List<Long> bucketDefinitionList;
		
		

		public VpnDataBucket(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.totalbytesFieldName = getConfigs().getString(config, "totalbytesFieldName");
			this.readbytesFieldName = getConfigs().getString(config, "readbytesFieldName");
			this.durationFieldName = getConfigs().getString(config, "durationFieldName");
			this.databucketFieldName = getConfigs().getString(config, "databucketFieldName");
			 List<String> bucketDefinition = getConfigs().getStringList(config, "bucketDefinition");
			parseBucketDefinition(bucketDefinition);
			
			validateArguments();
		}
		
		private void parseBucketDefinition(List<String> bucketDefinition){
			bucketDefinitionList = new ArrayList<>();
			long prev = 0;
			for(String def: bucketDefinition){
				Long bucketMax = Long.valueOf(def);
				
				if(bucketMax <= prev){
					break;
				}
				
				bucketDefinitionList.add(bucketMax);
				prev = bucketMax;
			}
		}

		@Override
		protected boolean doProcess(Record inputRecord) {
			Long duration = RecordExtensions.getLongValue(inputRecord, durationFieldName, DURATION_BUCKET_IN_SEC);

			int durationBucket = (int) ((duration-1)/DURATION_BUCKET_IN_SEC) + 1;
			
			Long readBytes = RecordExtensions.getLongValue(inputRecord, readbytesFieldName, null);
			if(readBytes == null){
				readBytes = RecordExtensions.getLongValue(inputRecord, totalbytesFieldName, 0L);
			}

			
			int readDataPerDurationBucket = (int) (readBytes/(durationBucket * MEGA_BYTE));
			long bucketValue = bucketDefinitionList.get(bucketDefinitionList.size()-1) * 2;
			for(Long bucketMax: bucketDefinitionList){
				if(bucketMax >= readDataPerDurationBucket){
					bucketValue = bucketMax;
					break;
				}
			}

			inputRecord.put(this.databucketFieldName, bucketValue);
			
			return super.doProcess(inputRecord);

		}
	}
}