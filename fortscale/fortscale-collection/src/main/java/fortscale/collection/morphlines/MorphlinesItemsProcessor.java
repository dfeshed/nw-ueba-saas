package fortscale.collection.morphlines;

import fortscale.collection.monitoring.ItemContext;
import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.MorphlineContext;
import org.kitesdk.morphline.api.Record;
import org.kitesdk.morphline.base.Compiler;
import org.kitesdk.morphline.base.Fields;
import org.kitesdk.morphline.base.Notifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.IOException;

/**
 * process items using a given kite (A.K.A morphline) conf file
 */
public class MorphlinesItemsProcessor implements Closeable {

	public static final String ITEM_CONTEXT = "ITEM_CONTEXT";
	private static Logger logger = LoggerFactory.getLogger(MorphlinesItemsProcessor.class);
	
	private Command morphline;
	private boolean isClosed = true;
	private RecordSinkCommand sinkCommand;

	public MorphlinesItemsProcessor(Resource config) throws IOException, IllegalArgumentException {
		// ensure required parameters are set
		Assert.isTrue(config != null, "morphline config file is required");
		Assert.isTrue(config.exists() && config.isReadable(), "morphline config file is not accesible: "+ config.getFilename());
		
		// create the morphline command to be used by this processor
		MorphlineContext morphlineContext = new MorphlineContext.Builder().build();
		sinkCommand = new RecordSinkCommand();
		morphline = new Compiler().compile(config.getFile(), null, morphlineContext, sinkCommand);
		open();
	}

	public Record process(Record record, ItemContext itemContext) {

		//Set item context
		if (record!=null){
			record.put(ITEM_CONTEXT, itemContext);
		}


		// re-open the morphline transaction is closed
		if (isClosed)
			open();
		
		// process the record
		boolean success = morphline.process(record);

		// get the record from the sink command so that we will receive only
		// record which are not been dropped on the way and with all the
		// properties set by the etl
		Record processed = sinkCommand.popRecord();

		//Clean item context
		if (processed!=null) {
			processed.removeAll(ITEM_CONTEXT);
		}

		if (!success) {
			logger.warn("error processing record {}", record);
			return null;
		}

		// return the result record
		return processed;
	}



	/**
	 *
	 * @param item - the record to process - mandatory
	 * @param itemContext - name of origion where the data come from, I.E. file name,
	 *               	for monitoring purposes. - itemContext can be null.
	 * @return
	 */
	public Record process(String item, ItemContext itemContext) {

		// create a record that holds the input string
		Record record = new Record();
		record.put(Fields.MESSAGE, item);
		return process(record, itemContext);

	}

	@Override
	public void close() throws IOException {
		Notifications.notifyShutdown(morphline);
		isClosed = true;
	}
	
	private void open() {
		if (isClosed) {
			Notifications.notifyBeginTransaction(morphline);
			isClosed = false;
		}
	}

}
