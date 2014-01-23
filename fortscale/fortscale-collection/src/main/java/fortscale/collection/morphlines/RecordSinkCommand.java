package fortscale.collection.morphlines;

import org.kitesdk.morphline.api.Command;
import org.kitesdk.morphline.api.Record;

public class RecordSinkCommand implements Command {
	
	private Record record;

	@Override
	public void notify(Record notification) {
	}

	@Override
	public boolean process(Record record) {
		this.record = record;
		return true;
	}

	@Override
	public Command getParent() {
		return null;
	}

	public Record popRecord() {

		Record value = this.record;
		this.record = null;

		return value;
	}

}
