package fortscale.utils.splunk;

import java.io.IOException;

import com.splunk.Event;

public class SplunkEventsHandlerDummy implements ISplunkEventsHandler {

	public void open() throws IOException {}

	public void close() throws IOException {}

	public void flush() throws IOException {}

	public void handle(Event event) throws IOException {}

	@Override
	public void setSearchReturnKeys(String searchReturnKeys) {}

	@Override
	public void setDelimiter(String delimiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDisableQuotes(boolean isDisableQuotes) {
		// TODO Auto-generated method stub
		
	}

}
