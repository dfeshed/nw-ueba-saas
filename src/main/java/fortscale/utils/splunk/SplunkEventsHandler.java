package fortscale.utils.splunk;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.splunk.Event;

public abstract class SplunkEventsHandler implements ISplunkEventsHandler{
	private static final String QUOTES_REPLACEMENT = "\\\"";
	private boolean isFirstEvent = true;
	private String[] searchReturnKeys = null;
	private boolean isSkipFirstLine = false;
	
	
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
    			val = val.replace("\"", QUOTES_REPLACEMENT);
    		}
    		sbuf.append("\"").append(val).append("\"").append(",");
    	}
    	write(sbuf.substring(0, sbuf.length()-1));
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
}
