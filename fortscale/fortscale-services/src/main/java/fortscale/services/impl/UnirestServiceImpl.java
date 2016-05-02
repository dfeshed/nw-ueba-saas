package fortscale.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import fortscale.services.UnirestService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * Created by Amir Keren on 5/2/16.
 */
public class UnirestServiceImpl<T> implements UnirestService<T>, InitializingBean {

	private static final Logger logger = Logger.getLogger(UnirestServiceImpl.class);

	private Class<T> type;

	/**
	 *
	 * This method sends a get request to a particular url
	 *
	 * @param url
	 * @param headers
	 * @return
	 * @throws UnirestException
	 */
	@Override
	public HttpResponse<T> sendSimpleGETRequest(String url, Map<String, String> headers)
			throws UnirestException {
		return sendGETRequest(url, headers, null, null);
	}

	/**
	 *
	 * This method sends a post request to a particular url
	 *
	 * @param url
	 * @param headers
	 * @param fields
	 * @return
	 * @throws UnirestException
	 */
	@Override
	public HttpResponse<T> sendSimplePOSTRequest(String url, Map<String, String> headers,
			Map<String, String> fields) throws UnirestException {
		return sendPOSTRequest(url, headers, null, null, fields);
	}

	/**
	 *
	 * This method sends a get request to a particular url
	 *
	 * @param url
	 * @param headers
	 * @param queryStrings
	 * @param routeParams
	 * @return
	 * @throws UnirestException
	 */
	@Override
	public HttpResponse<T> sendGETRequest(String url, Map<String, String> headers,
			Map<String, String> queryStrings, Map<String, String> routeParams) throws UnirestException {
		return processRequest(Unirest.get(url), headers, queryStrings, routeParams);
	}

	/**
	 *
	 * This method sends a post request to a particular url
	 *
	 * @param url
	 * @param headers
	 * @param queryStrings
	 * @param fields
	 * @param routeParams
	 * @return
	 * @throws UnirestException
	 */
	@Override
	public HttpResponse<T> sendPOSTRequest(String url, Map<String, String> headers,
			Map<String, String> queryStrings, Map<String, String> routeParams, Map<String, String> fields)
			throws UnirestException {
		return processRequest(Unirest.post(url), headers, queryStrings, routeParams, fields);
	}

	/**
	 *
	 * Auxiliary method to include headers and urlfields
	 *
	 * @param request
	 * @param headers
	 * @param queryStrings
	 * @param routeParams
	 * @return
	 * @throws UnirestException
	 */
	private HttpResponse<T> processRequest(HttpRequest request, Map<String, String> headers,
			Map<String, String> queryStrings, Map<String, String> routeParams) throws UnirestException {
		setRouteParams(request, routeParams);
		setHeaders(request, headers);
		setQueryStrings(request, queryStrings);
		return request.asObject(type);
	}

	/**
	 *
	 * Auxiliary method to include headers, querystrings, routeparams and fields
	 *
	 * @param request
	 * @param headers
	 * @param queryStrings
	 * @param routeParams
	 * @param fields
	 * @return
	 * @throws UnirestException
	 */
	private HttpResponse<T> processRequest(HttpRequest request, Map<String, String> headers,
			Map<String, String> queryStrings, Map<String, String> routeParams, Map<String, String> fields)
			throws UnirestException {
		setFields(request, fields);
		return processRequest(request, headers, queryStrings, routeParams);
	}

	/**
	 *
	 * This method sets the map of headers to the request
	 *
	 * @param request
	 * @param headers
	 */
	private void setHeaders(HttpRequest request, Map<String, String> headers) {
		if (headers != null && !headers.isEmpty()) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				request.header(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 *
	 * This method sets the map of route params to the request
	 *
	 * @param request
	 * @param routeParams
	 */
	private void setRouteParams(HttpRequest request, Map<String, String> routeParams) {
		if (routeParams != null && !routeParams.isEmpty()) {
			for (Map.Entry<String, String> entry : routeParams.entrySet()) {
				request.routeParam(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 *
	 * This method sets the map of querystrings to the request
	 *
	 * @param request
	 * @param queryStrings
	 */
	private void setQueryStrings(HttpRequest request, Map<String, String> queryStrings) {
		if (queryStrings != null && !queryStrings.isEmpty()) {
			for (Map.Entry<String, String> entry : queryStrings.entrySet()) {
				request.queryString(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 *
	 * This method sets the map of fields to the request
	 *
	 * @param request
	 * @param fields
	 */
	private void setFields(HttpRequest request, Map<String, String> fields) {
		if (fields != null && !fields.isEmpty() && request instanceof HttpRequestWithBody) {
			HttpRequestWithBody tempRequest = (HttpRequestWithBody)request;
			for (Map.Entry<String, String> entry : fields.entrySet()) {
				tempRequest.field(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * This method acts as a constructor
	 *
	 * @throws Exception
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.type = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Unirest.setObjectMapper(new ObjectMapper() {

			private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.
					databind.ObjectMapper();

			public <T> T readValue(String value, Class<T> valueType) {
				try {
					return jacksonObjectMapper.readValue(value, valueType);
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}

			public String writeValue(Object value) {
				try {
					return jacksonObjectMapper.writeValueAsString(value);
				} catch (JsonProcessingException ex) {
					throw new RuntimeException(ex);
				}
			}

		});
	}

}