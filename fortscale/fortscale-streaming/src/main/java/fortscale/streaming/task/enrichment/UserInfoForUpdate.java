package fortscale.streaming.task.enrichment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by idanp on 2/3/2015.
 */

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY, getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE)
public class UserInfoForUpdate {


	public Map<String,Pair<Long,String>> userInfo;


	//CTOs
	@JsonCreator
	public UserInfoForUpdate() {

		this.userInfo = new HashMap<>();

	}


	public Map<String, Pair<Long, String>> getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Map<String, Pair<Long, String>> userInfo) {
		this.userInfo = userInfo;
	}
}
