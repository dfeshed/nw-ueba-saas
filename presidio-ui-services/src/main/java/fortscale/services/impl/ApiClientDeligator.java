package fortscale.services.impl;

import fortscale.utils.logging.Logger;
import fortscale.utils.spring.SpringPropertiesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.security.access.method.P;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.ApiException;
import presidio.output.client.client.JSON;
import presidio.output.client.client.Pair;
import presidio.output.client.client.auth.Authentication;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by shays on 10/08/2017.
 */
public class ApiClientDeligator extends ApiClient{

    private ApiClient apiClient;

    protected Logger logger = Logger.getLogger(this.getClass());

    public ApiClientDeligator(ApiClient apiClient){
        this.apiClient = apiClient;
        setUserAgent("Swagger-Codegen/1.0.0/java");
    }

    /**
     * Gets the JSON instance to do JSON serialization and deserialization.
     * @return JSON
     */
    public JSON getJSON() {
        return apiClient.getJSON();
    }

    public Client getHttpClient() {
        return apiClient.getHttpClient();
    }

    public ApiClient setHttpClient(Client httpClient) {
        return apiClient.setHttpClient(httpClient);
    }

    public String getBasePath() {
        return apiClient.getBasePath();
    }

    public ApiClient setBasePath(String basePath) {
        return apiClient.setBasePath(basePath);
    }

    /**
     * Gets the status code of the previous request
     * @return Status code
     */
    public int getStatusCode() {
        return apiClient.getStatusCode();
    }

    /**
     * Gets the response headers of the previous request
     * @return Response headers
     */
    public Map<String, List<String>> getResponseHeaders() {
        return apiClient.getResponseHeaders();
    }

    /**
     * Get authentications (key: authentication name, value: authentication).
     * @return Map of authentication object
     */
    public Map<String, Authentication> getAuthentications() {
        return apiClient.getAuthentications();
    }

    /**
     * Get authentication for the given name.
     *
     * @param authName The authentication name
     * @return The authentication, null if not found
     */
    public Authentication getAuthentication(String authName) {
        return apiClient.getAuthentication(authName);
    }

    /**
     * Helper method to set username for the first HTTP basic authentication.
     * @param username Username
     */
    public void setUsername(String username) {
        apiClient.setUsername(username);
    }

    /**
     * Helper method to set password for the first HTTP basic authentication.
     * @param password Password
     */
    public void setPassword(String password) {
        apiClient.setPassword(password);
    }

    /**
     * Helper method to set API key value for the first API key authentication.
     * @param apiKey API key
     */
    public void setApiKey(String apiKey) {
        apiClient.setApiKey(apiKey);
    }

    /**
     * Helper method to set API key prefix for the first API key authentication.
     * @param apiKeyPrefix API key prefix
     */
    public void setApiKeyPrefix(String apiKeyPrefix) {
        apiClient.setApiKeyPrefix(apiKeyPrefix);
    }

    /**
     * Helper method to set access token for the first OAuth2 authentication.
     * @param accessToken Access token
     */
    public void setAccessToken(String accessToken) {
        apiClient.setAccessToken(accessToken);
    }

    /**
     * Set the User-Agent header's value (by adding to the default header map).
     * @param userAgent Http user agent
     * @return API client
     */
    public ApiClient setUserAgent(String userAgent) {
        if (apiClient!=null) {
            return apiClient.setUserAgent(userAgent);
        }
        return this;
    }

    /**
     * Add a default header.
     *
     * @param key The header's key
     * @param value The header's value
     * @return API client
     */
    public ApiClient addDefaultHeader(String key, String value) {
        return apiClient.addDefaultHeader(key, value);
    }

    /**
     * Check that whether debugging is enabled for this API client.
     * @return True if debugging is switched on
     */
    public boolean isDebugging() {
        return apiClient.isDebugging();
    }

    /**
     * Enable/disable debugging for this API client.
     *
     * @param debugging To enable (true) or disable (false) debugging
     * @return API client
     */
    public ApiClient setDebugging(boolean debugging) {
        return apiClient.setDebugging(debugging);
    }

    /**
     * The path of temporary folder used to store downloaded files from endpoints
     * with file response. The default value is <code>null</code>, i.e. using
     * the system's default tempopary folder.
     *
     * @return Temp folder path
     */
    public String getTempFolderPath() {
        return apiClient.getTempFolderPath();
    }

    /**
     * Set temp folder path
     * @param tempFolderPath Temp folder path
     * @return API client
     */
    public ApiClient setTempFolderPath(String tempFolderPath) {
        return apiClient.setTempFolderPath(tempFolderPath);
    }

    /**
     * Connect timeout (in milliseconds).
     * @return Connection timeout
     */
    public int getConnectTimeout() {
        return apiClient.getConnectTimeout();
    }

    /**
     * Set the connect timeout (in milliseconds).
     * A value of 0 means no timeout, otherwise values must be between 1 and
     * {@link Integer#MAX_VALUE}.
     * @param connectionTimeout Connection timeout in milliseconds
     * @return API client
     */
    public ApiClient setConnectTimeout(int connectionTimeout) {
        return apiClient.setConnectTimeout(connectionTimeout);
    }

    /**
     * Get the date format used to parse/format date parameters.
     * @return Date format
     */
    public DateFormat getDateFormat() {
        return apiClient.getDateFormat();
    }

    /**
     * Set the date format used to parse/format date parameters.
     * @param dateFormat Date format
     * @return API client
     */
    public ApiClient setDateFormat(DateFormat dateFormat) {
        return apiClient.setDateFormat(dateFormat);
    }

    /**
     * Parse the given string into Date object.
     * @param str String
     * @return Date
     */
    public Date parseDate(String str) {
        return apiClient.parseDate(str);
    }

    /**
     * Format the given Date object into string.
     * @param date Date
     * @return Date in string format
     */
    public String formatDate(Date date) {
        return apiClient.formatDate(date);
    }

    /**
     * Format the given parameter object into string.
     * @param param Object
     * @return Object in string format
     */
    public String parameterToString(Object param) {
        return apiClient.parameterToString(param);
    }

    public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
        return apiClient.parameterToPairs(collectionFormat, name, value);
    }

    /**
     * Check if the given MIME is a JSON MIME.
     * JSON MIME examples:
     *   application/json
     *   application/json; charset=UTF8
     *   APPLICATION/JSON
     *   application/vnd.company+json
     * @param mime MIME
     * @return True if the MIME type is JSON
     */
    public boolean isJsonMime(String mime) {
        return apiClient.isJsonMime(mime);
    }

    /**
     * Select the Accept header's value from the given accepts array:
     *   if JSON exists in the given array, use it;
     *   otherwise use all of them (joining into a string)
     *
     * @param accepts The accepts array to select from
     * @return The Accept header to use. If the given array is empty,
     *   null will be returned (not to set the Accept header explicitly).
     */
    public String selectHeaderAccept(String[] accepts) {
        if (accepts == null || accepts.length==0){
            accepts = new String[]{"application/json"};
        }
        return apiClient.selectHeaderAccept(accepts);
    }

    /**
     * Select the Content-Type header's value from the given array:
     *   if JSON exists in the given array, use it;
     *   otherwise use the first one of the array.
     *
     * @param contentTypes The Content-Type array to select from
     * @return The Content-Type header to use. If the given array is empty,
     *   JSON will be used.
     */
    public String selectHeaderContentType(String[] contentTypes) {
        return apiClient.selectHeaderContentType(contentTypes);
    }

    /**
     * Escape the given string to be used as URL query value.
     * @param str String
     * @return Escaped string
     */
    public String escapeString(String str) {
        return apiClient.escapeString(str);
    }

    /**
     * Serialize the given Java object into string entity according the given
     * Content-Type (only JSON is supported for now).
     * @param obj Object
     * @param formParams Form parameters
     * @param contentType Context type
     * @return Entity
     * @throws ApiException API exception
     */
    public Entity<?> serialize(Object obj, Map<String, Object> formParams, String contentType) throws ApiException {
        return apiClient.serialize(obj, formParams, contentType);
    }

    /**
     * Deserialize response body to Java object according to the Content-Type.
     * @param response Response
     * @param returnType Return type
     * @return Deserialize object
     * @throws ApiException API exception
     */
    public <T> T deserialize(Response response, GenericType<T> returnType) throws ApiException {
        return apiClient.deserialize(response, returnType);
    }

    /**
     * Download file from the given response.
     * @param response Response
     * @return File
     * @throws ApiException If fail to read file content from response and write to disk
     */
    public File downloadFileFromResponse(Response response) throws ApiException {
        return apiClient.downloadFileFromResponse(response);
    }

    public File prepareDownloadFile(Response response) throws IOException {
        return apiClient.prepareDownloadFile(response);
    }

    /**
     * Invoke API by sending HTTP request with the given options.
     *
     * @param path The sub-path of the HTTP URL
     * @param method The request method, one of "GET", "POST", "PUT", and "DELETE"
     * @param queryParams The query parameters
     * @param body The request body object
     * @param headerParams The header parameters
     * @param formParams The form parameters
     * @param accept The request's Accept header
     * @param contentType The request's Content-Type header
     * @param authNames The authentications to apply
     * @param returnType The return type into which to deserialize the response
     * @return The response body in type of string
     * @throws ApiException API exception
     */
    public <T> T invokeAPI(String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, Object> formParams, String accept, String contentType, String[] authNames, GenericType<T> returnType) throws ApiException {
        if (headerParams==null) {
            headerParams=new HashMap<>();
        }
        if (method.equals("GET")&&body!=null) {
            List<Pair> queryValues = buildQueryParams( body);
            queryParams = updateQueryParams(queryParams, queryValues);
        }
        headerParams.put("Content-Type","application/json");

        logRequest(path,method,queryParams,body,headerParams,formParams,accept,contentType);

        return apiClient.invokeAPI(path, method, queryParams, body, headerParams, formParams, accept, contentType, authNames, returnType);
    }

    private void logRequest(String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, Object> formParams, String accept, String contentType) {
        if (method.equals("GET")){
            String getRequestURL=getBasePath()+"/"+path;
            if (queryParams!=null){
                getRequestURL+="/";
                for (Pair param :queryParams){
                    getRequestURL+=param.getName()+"="+param.getValue()+"&";
                }
            }
            logger.info("Going to send GET request with following params: query{} \n header: {} \n content type {} \n accept",getRequestURL,headerParams,contentType, accept);

        } else {
            logger.info("Going to send request with \n method: {} \n to path {} \n with queryParams {} \n headerParams: keys-{} - values-{} \n accept: {}, \n content type: {}",
                    method,path,StringUtils.join(queryParams,","),StringUtils.join(headerParams.keySet(),","),StringUtils.join(headerParams.values(),","),
                    accept,contentType);
        }
    }

    private List<Pair> updateQueryParams(List<Pair> queryParams, List<Pair> queryValues) {
        if (CollectionUtils.isNotEmpty(queryValues)) {
            if (queryParams == null) {
                queryParams = queryValues;
            } else {
                queryParams.addAll(queryValues);
            }

        }
        return queryParams;
    }

    private List<Pair> buildQueryParams(Object body) {
        try {
            List<PropertyDescriptor> propertyDescriptors = Arrays.asList(
                    Introspector.getBeanInfo(body.getClass(), Object.class)
                            .getPropertyDescriptors()
            );


            List<Pair> queryValues = new ArrayList<>();
            for (PropertyDescriptor pd : propertyDescriptors) {

                String stringValue = readPropertyValueAsString(body, pd);

                if (StringUtils.isNotBlank(stringValue)) {
                    queryValues.add(new Pair(pd.getName(), stringValue));
                }


            }

           return queryValues;
        } catch (Exception e) {
            throw new RuntimeException("Cannot parse query", e);
        }

    }

    private String readPropertyValueAsString(Object body,PropertyDescriptor pd) throws IllegalAccessException, InvocationTargetException {
        Method readMethod=pd.getReadMethod();
        String stringValue = null;
        if (readMethod!=null){
            Object value=readMethod.invoke(body);
            if (value!=null){
                stringValue=convertValueToString(value);
            }
        }
        return stringValue;
    }

    private String convertValueToString(Object value) {
        if (value==null){
            return null;
        }
        if (value instanceof String){
            return  (String)value;
        }


        if (value instanceof Collection){
            Collection values=(Collection)value;
            if (CollectionUtils.isEmpty(values)){
                return "";
            }
            List<String> strings = new ArrayList<>();
            ((Collection) value).forEach(x->{
                if (x.getClass().isEnum()){
                    strings.add(((Enum)x).name());
                } else {
                    strings.add((x.toString()));
                }
            });

            return StringUtils.join(strings,",");
        }
        if (value instanceof Boolean){
            return Boolean.toString((Boolean)value);
        }

        if (value instanceof Number){
            return value.toString();
        }
        return value.toString();
    }
}
