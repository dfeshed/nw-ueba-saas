package fortscale.streaming.task;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class VPNSessionEventsModelStreamTask extends EventsPrevalenceModelStreamTask {

	private static final String CLOSED = "CLOSED";
	private static final String STATUS_FIELD = "status";
    private static final String COUNTRYFIELD = "country";
    private static final String ReservedRange = "Reserved Range";
	
	@Override
	protected boolean acceptMessage(JSONObject message) {


        //Create new field for modeling country feature -
        //If its ReservedRange then this field will be empty and will get 0 at the score else this field will contain the country value
        if(ReservedRange.equals(message.get(COUNTRYFIELD)))
            message.put("country_to_score","");


        else
            message.put("country_to_score",message.get(COUNTRYFIELD));




		// filter out vpn events which are not in closed status
		return CLOSED.equals(message.get(STATUS_FIELD));
	}
	
}
