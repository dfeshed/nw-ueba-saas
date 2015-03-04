package fortscale.collection.morphlines.commands;

import com.typesafe.config.Config;
import fortscale.collection.morphlines.RecordExtensions;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.CommandBuilder;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.AbstractCommand;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Deprecated
@Configurable(preConstruction=true)
public class VpnContinuousDataBucketBuilder implements CommandBuilder {

	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("VpnContinuousDataBucket");
	}

	@Override
	public Command build(Config config, Command parent, Command child, MorphlineContext context) {
		return new VpnContinuousDataBucket(this, config, parent, child, context);
	}


	private class VpnContinuousDataBucket extends AbstractCommand {

		private String totalbytesFieldName;
		private String readbytesFieldName;
		private String durationFieldName;
		private String databucketFieldName;



		public VpnContinuousDataBucket(CommandBuilder builder, Config config, Command parent, Command child, MorphlineContext context) {
			super(builder, config, parent, child, context);
			this.totalbytesFieldName = getConfigs().getString(config, "totalbytesFieldName");
			this.readbytesFieldName = getConfigs().getString(config, "readbytesFieldName");
			this.durationFieldName = getConfigs().getString(config, "durationFieldName");
			this.databucketFieldName = getConfigs().getString(config, "databucketFieldName");

			validateArguments();
		}



		@Override
		protected boolean doProcess(Record inputRecord) {

			// get duration
			Long duration = RecordExtensions.getLongValue(inputRecord, durationFieldName, null);

			// get bytes (get "total" if there are no "read" bytes)
			Long readBytes = RecordExtensions.getLongValue(inputRecord, readbytesFieldName, null);
			if(readBytes == null){
				readBytes = RecordExtensions.getLongValue(inputRecord, totalbytesFieldName, 0L);
			}

			// calculate bucket - in case that we don't have duration, we will not add the bucket field and the score will be 0
			if(duration != null ){
				if(duration > 0){
					Long bytePerSec = (Long.valueOf(readBytes)/(20*60 + duration));
					inputRecord.put(this.databucketFieldName, bytePerSec);
				}
			}
			return getChild().process(inputRecord);

		}
	}
}