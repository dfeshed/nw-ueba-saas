package fortscale.collection.morphlines;

import java.io.IOException;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Fields;
import org.kitesdk.morphline.base.Compiler;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import fortscale.collection.ItemsProcessor;

/**
 * process items using a given kite (A.K.A morphline) conf file
 */
public class MorphlinesItemsProcessor implements ItemsProcessor {

	private Command morphline;
	private RecordSinkCommand sinkCommand;

	public MorphlinesItemsProcessor(Resource config) throws IOException {
		// ensure required parameters are set
		Assert.isTrue(config != null, "morphline config file is required");
		Assert.isTrue(config.exists() && config.isReadable(),
				"morphline config file is not accesible");
		
		// create the morphline command to be used by this processor
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		sinkCommand = new RecordSinkCommand();
		morphline = new Compiler().compile(config.getFile(), null, morphlineContext, sinkCommand);
	}

	@Override
	public Object process(Object item) {

		// create a record that holds the input string
		Record record = new Record();
		record.put(Fields.MESSAGE, item);

		// process the record
		boolean success = morphline.process(record);

		// get the record from the sink command so that we will receive only
		// record which are not been dropped on the way and with all the
		// properties set by the etl
		Record processed = sinkCommand.popRecord();

		if (!success) {
			// TODO: log error processing record
			return null;
		}

		// return the result record
		return processed;
	}

}
