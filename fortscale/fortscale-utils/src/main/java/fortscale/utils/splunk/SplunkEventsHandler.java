package fortscale.utils.splunk;

import com.splunk.Event;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public abstract class SplunkEventsHandler implements ISplunkEventsHandler {

	private static final String QUOTES_REPLACEMENT = "\\\"";
	private boolean isFirstEvent = true;
	private String[] searchReturnKeys = null;
	private boolean isSkipFirstLine = false;
	private String delimiter = ",";
	private boolean isDisableQuotes = false;
	private boolean forceSingleLineEvents = false;
	private boolean surroundKeyWithQuotes = false;

	public abstract void open() throws IOException;
	public abstract void close() throws Exception;
	public abstract void flush() throws IOException;
	public abstract void write(String str) throws IOException;
	public abstract void newLine() throws IOException;

	public void handle(Event event) throws IOException {
		if(isFirstEvent){
			if(searchReturnKeys == null || searchReturnKeys.length == 0){
				searchReturnKeys = event.keySet().toArray(new String[0]);
			}
			if(!isSkipFirstLine){
				write(StringUtils.join(searchReturnKeys,","));
				newLine();
				isFirstEvent = false;
			}
        }
    	StringBuffer sbuf = new StringBuffer();
    	for(String key: searchReturnKeys){
    		String val = event.get(key);
    		if(val == null){
    			val = "";
    		} else{
    			if(!isDisableQuotes){
    				val = val.replace("\"", QUOTES_REPLACEMENT);
    			}
    			
    			if (forceSingleLineEvents) {
    				val = val.replace('\n', '\t');
    			}
    		}
    		if(!isDisableQuotes){
    			sbuf.append("\"");
    		}
    		sbuf.append(val);
    		if(!isDisableQuotes){
    			sbuf.append("\"");
    		}
    		sbuf.append(delimiter);
    	}
    	write(sbuf.substring(0, sbuf.length()-delimiter.length()));
    	newLine();
	}
	
	public String getSearchReturnKeys() {
		return StringUtils.join(searchReturnKeys, ',');
	}

	public void setSearchReturnKeys(String searchReturnKeys) {
		if(searchReturnKeys != null){
			this.searchReturnKeys = searchReturnKeys.split(",");
		}
	}

	public boolean isSkipFirstLine() {
		return isSkipFirstLine;
	}

	public void setSkipFirstLine(boolean isSkipFirstLine) {
		this.isSkipFirstLine = isSkipFirstLine;
	}

	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isDisableQuotes() {
		return isDisableQuotes;
	}
	
	public void setForceSingleLineEvents(boolean forceSingleLineEvents) {
		this.forceSingleLineEvents = forceSingleLineEvents;
	}
	
	public boolean isForceSingleLineEvents() {
		return this.forceSingleLineEvents;
	}
	
	@Override
	public void setDisableQuotes(boolean isDisableQuotes) {
		this.isDisableQuotes = isDisableQuotes;
	}
	
	public boolean isSurroundKeyWithQuotes() {
		return this.surroundKeyWithQuotes;
	}
	
	public void setSurroundKeyWithQuotes(boolean surroundKeyWithQuotes) {
		this.surroundKeyWithQuotes = surroundKeyWithQuotes;
	}

}