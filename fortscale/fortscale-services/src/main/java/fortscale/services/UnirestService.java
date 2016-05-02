package fortscale.services;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Map;

/**
 * Created by Amir Keren on 5/2/16.
 */
public interface UnirestService<T> {

	HttpResponse<T> sendSimpleGETRequest(String url, Map<String, String> headers) throws UnirestException;
	HttpResponse<T> sendSimplePOSTRequest(String url, Map<String, String> headers, Map<String, String> fields)
			throws UnirestException;
	HttpResponse<T> sendGETRequest(String url, Map<String, String> headers, Map<String, String> queryStrings,
			Map<String, String> routeParams) throws UnirestException;
	HttpResponse<T> sendPOSTRequest(String url, Map<String, String> headers, Map<String, String> queryStrings,
			Map<String, String> routeParams, Map<String, String> fields) throws UnirestException;

}