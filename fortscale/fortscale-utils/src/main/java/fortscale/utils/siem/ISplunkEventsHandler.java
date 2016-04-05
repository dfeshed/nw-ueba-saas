package fortscale.utils.siem;

import com.splunk.Event;

import java.io.IOException;

public interface ISplunkEventsHandler {

	void open() throws IOException;
	void close() throws Exception;
	void flush() throws IOException;
	void handle(Event event) throws IOException;
	void setSearchReturnKeys(String searchReturnKeys);
	void setDelimiter(String delimiter);
	void setDisableQuotes(boolean isDisableQuotes);

}