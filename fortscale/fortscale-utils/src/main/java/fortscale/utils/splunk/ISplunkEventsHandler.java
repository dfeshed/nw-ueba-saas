package fortscale.utils.splunk;

import java.io.IOException;

import com.splunk.Event;




public interface ISplunkEventsHandler {
	public void open() throws IOException;
	public void close() throws Exception;
	public void flush() throws IOException;
	public void handle(Event event) throws IOException;
	public void setSearchReturnKeys(String searchReturnKeys);
	public void setDelimiter(String delimiter);
	public void setDisableQuotes(boolean isDisableQuotes);
}