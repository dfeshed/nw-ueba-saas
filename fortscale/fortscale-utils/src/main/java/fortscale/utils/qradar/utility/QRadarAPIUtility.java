package fortscale.utils.qradar.utility;

import fortscale.utils.logging.Logger;
import fortscale.utils.qradar.requests.CreateSearchRequest;
import fortscale.utils.qradar.requests.GenericRequest;
import fortscale.utils.qradar.requests.SearchInformationRequest;
import fortscale.utils.qradar.requests.SearchResultRequest;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class QRadarAPIUtility {

	private static final Logger logger = Logger.getLogger(QRadarAPIUtility.class);

	static String className;

	public static String PROTOCOL = "https";
	public static String URL = "/restapi/api/ariel/searches";

	private static final String prop_CreateSearch_Method = "POST";
	private static final String prop_RetrieveSearchResultBySearchID_Method = "GET";
	private static final String prop_RetrieveSearchInformationBySearchID_Method = "GET";

	private static final int RESPONSE_SUCCESS = 200;
	private static final int RESPONSE_CREATED = 201;
	private static final int RESPONSE_DOESNOTEXIST = 404;
	private static final int RESPONSE_ALREADYEXISTS = 400;
	private static final int RESPONSE_INVALIDPARAM = 422;
	private static final int RESPONSE_ERROR = 500;

	private static final String EVENTS_HEADER = "events:[";

	/**
	 *
	 * This is a helper method for encoding URL params in UTF-8
	 *
	 * @param inString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeURLParams(String inString) throws UnsupportedEncodingException {
		return URLEncoder.encode(inString, "UTF-8");
	}

	/**
	 *
	 * This method builds the URL with parameters
	 *
	 * @param inURL
	 * @param inArrayList
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String formURLWithParameters(String inURL, ArrayList<ParameterDefinition> inArrayList)
			throws UnsupportedEncodingException {
		String framedURL = inURL;
		Iterator<ParameterDefinition> inArrayInPathIterator = inArrayList.iterator();
		// Extract all InPath parameters first
		logger.debug(className + " --> Fetching all Parameters and values for Processing......");
		while (inArrayInPathIterator.hasNext()) {
			ParameterDefinition pd = inArrayInPathIterator.next();
			logger.debug(className + " --> Parameter: " + pd.getParamName() + " ## value: " +
					pd.getParamValue());
			if (pd.isInPath()) {
				if (pd.isRequired()) {
					if (pd.getParamValue() != null) {
						logger.debug("           ######Added " + pd.getParamName() + " in URL Path");
						framedURL = (pd.isencoded()) ? framedURL + "/" + encodeURLParams(pd.getParamValue()) :
								framedURL + "/" + pd.getParamValue();
						logger.debug("           ######Intermediate URL after adding parameter: " + framedURL);
						inArrayInPathIterator.remove();
					} else {
						logger.debug("Error. " + pd.getParamName() + " is a required parameter");
						System.exit(0);
					}
				} else {
					if (pd.getParamValue() != null) {
						logger.debug("           ######Added " + pd.getParamName() + " in URL Path");
						framedURL = (pd.isencoded()) ? framedURL + "/" + encodeURLParams(pd.getParamValue()) :
								framedURL + "/" + pd.getParamValue();
						logger.debug("           ######Intermediate URL after adding parameter: " + framedURL);
						inArrayInPathIterator.remove();
					}
				}
			} else {
				logger.debug("           ######" + pd.getParamName() + " will be processed later as a URL Parameter");
			}
		}
		logger.debug("Begin Processing all Parameters that were skipped earlier to be added as URL " + "parameter....");
		String paramList = "?";
		Iterator<ParameterDefinition> inArrayInParamIterator = inArrayList.iterator();
		while (inArrayInParamIterator.hasNext()) {
			ParameterDefinition pd = inArrayInParamIterator.next();
			if (!pd.isInPath()) {
				if (pd.isRequired()) {
					if (pd.getParamValue() != null) {
						paramList = (pd.isencoded()) ? (paramList.length() == 1) ? (paramList + pd.getParamName() +
								"=" + encodeURLParams(pd.getParamValue())) : (paramList + "&" + pd.getParamName() +
								"=" + encodeURLParams(pd.getParamValue())) : (paramList.length() == 1) ? (paramList +
								pd.getParamName() + "=" + pd.getParamValue()) : (paramList + "&" + pd.getParamName() +
								"=" + pd.getParamValue());
						logger.debug(className + "######Adding a Parameter to the URL: " + pd.getParamName() +
								"(" + pd.getParamValue() + ")");
						logger.debug(className + "######List of Parameters: " + paramList);
					} else {
						logger.debug("Error. " + pd.getParamName() + " is a required parameter");
						System.exit(0);
					}
				} else {
					if (pd.getParamValue() != null) {
						paramList = (pd.isencoded()) ? (paramList.length() == 1) ? (paramList + pd.getParamName() + "="
								+ encodeURLParams(pd.getParamValue())) : (paramList + "&" + pd.getParamName() + "=" +
								encodeURLParams(pd.getParamValue())) : (paramList.length() == 1) ? (paramList +
								pd.getParamName() + "=" + pd.getParamValue()) : (paramList + "&" + pd.getParamName() +
								"=" + pd.getParamValue());
						logger.debug(className + "######Adding a Parameter to the URL : " + pd.getParamName() +
								"(" + pd.getParamValue() + ")");
						logger.debug(className + "######List of Parameters: " + paramList);
					}
				}
			}
		}
		if (paramList.length() > 1) {
			logger.debug(className + " URL Returned ------>" + framedURL + paramList);
			return framedURL + paramList;
		} else {
			logger.debug(className + " URL Returned ------>" + framedURL);
			return framedURL;
		}
	}

	/**
	 *
	 * This method sends the actual request to QRadar
	 *
	 * @param hostname
	 * @param token
	 * @param request
	 * @param returnJson
	 * @return
	 * @throws IOException
	 */
	public static String sendRequest(String hostname, String token, GenericRequest request, boolean returnJson)
			throws IOException {
		//During handshaking, if the URL's hostname and the server's identification hostname mismatch,
		//the verification mechanism can call back to implementers of this interface to determine if this
		//connection should be allowed.
		//We return true in all cases.
		HostnameVerifier hv = (urlHostName, session) -> true;
		// Set this property to the location of the cert file(if required)
		// System.setProperty("javax.net.ssl.trustStore", "certfile.cert");
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
		//Add parameters to the simple URL
		String urlString = addParameters(QRadarAPIUtility.URL, request);
		logger.debug("URL : " + urlString);
		//Construct URL with the Protocol name, serverIP and the parametrized URL
		java.net.URL url = new URL(QRadarAPIUtility.PROTOCOL, hostname, urlString);
		//Open connection to the constrtuced URL
		HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
		logger.info("sending request...");
		//Set Method -POST,GET OR DELETE
		String method = null;
		switch (request.getRequestType()) {
		case create_search:
			method = prop_CreateSearch_Method;
			break;
		case search_result: {
			method = prop_RetrieveSearchResultBySearchID_Method;
			SearchResultRequest _request = (SearchResultRequest) request;
			if (_request.getRangeStart() >= 0 && _request.getRangeEnd() > 0 &&
					_request.getRangeEnd() >= _request.getRangeStart()) {
				String items = "items=" + _request.getRangeStart() + "-" + _request.getRangeEnd();
				urlConn.setRequestProperty("Range", items);
			}
			break;
		}
		case search_information:
			method = prop_RetrieveSearchInformationBySearchID_Method;
			break;
		}
		urlConn.setRequestMethod(method);
		urlConn.setAllowUserInteraction(false);
		urlConn.setDoOutput(true);
		//Pass the security token generated from QRadar Authentication Services
		urlConn.setRequestProperty("sec", token);
		//Get Response Code and Message
		int rspCode = urlConn.getResponseCode();
		logger.info("Response Code = " + rspCode);
		logger.info("Response Message : " + urlConn.getResponseMessage());
		StringBuilder sb = new StringBuilder();
		//Check if response received is RESPONSE_SUCCESS
		if (rspCode == RESPONSE_SUCCESS || rspCode == RESPONSE_CREATED) {
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String nextLine = br.readLine();
			while (nextLine != null) {
				if (!returnJson) {
					nextLine = unjsonify(nextLine);
				}
				sb.append(nextLine).append("\n");
				nextLine = br.readLine();
			}
		} else {
			switch (rspCode) {
			case RESPONSE_DOESNOTEXIST:
				logger.error("The search does not exist");
				break;
			case RESPONSE_ERROR:
				logger.error("An error occurred while attempting to retrieve the data");
				break;
			case RESPONSE_INVALIDPARAM:
				logger.error("A request parameter is not valid or Search result not " +
						"found, the search is still in progress.");
				break;
			case RESPONSE_ALREADYEXISTS:
				logger.error("The search could not be created, the searchID provided is " +
						"already in use. Please use a unique SearchID.");
				break;
			}
		}
		return sb.toString();
	}

	/**
	 *
	 * This method sends the actual request to QRadar
	 *
	 * @param hostname
	 * @param token
	 * @param request
	 * @param returnJson
	 * @param maxNumberOfRetries
	 * @param sleepInMilliseconds
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String sendRequest(String hostname, String token, GenericRequest request, boolean returnJson,
			int maxNumberOfRetries, long sleepInMilliseconds) throws IOException, InterruptedException {
		// Retry variables
		boolean isRequestSuccessful = false;
		int retryNumber = 0;
		String result = "";
		// Send request
		while (!isRequestSuccessful && maxNumberOfRetries > retryNumber) {
			result = QRadarAPIUtility.sendRequest(hostname, token, request, returnJson);
			// If a response was received, finish sending.
			if (result != null && !result.equals("")) {
				isRequestSuccessful = true;
			} else {
				retryNumber++;
				Thread.sleep(sleepInMilliseconds);
			}
		}
		return result;
	}

	/**
	 *
	 * This method adds parameters to the REST API call
	 *
	 * @param inURL
	 * @param genericRequest
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String addParameters(String inURL, GenericRequest genericRequest)
			throws UnsupportedEncodingException {
		ArrayList<ParameterDefinition> listOfParams = new ArrayList();
		//Parameter Name,Parameter Value,IsInpath, IsRequired,IsEncoded
		switch (genericRequest.getRequestType()) {
			case create_search: {
				CreateSearchRequest request = (CreateSearchRequest) genericRequest;
				listOfParams.add(new ParameterDefinition("query_expression", request.getQuery(), false, true, true));
				break;
			}
			case search_result: {
				SearchResultRequest request = (SearchResultRequest) genericRequest;
				listOfParams.add(new ParameterDefinition("SearchID", request.getSearchId(), true, true, true));
				listOfParams.add(new ParameterDefinition("results", "results", true, true, false));
				break;
			}
			case search_information: {
				SearchInformationRequest request = (SearchInformationRequest) genericRequest;
				listOfParams.add(new ParameterDefinition("SearchID", request.getSearchId(), true, true, true));
				break;
			}
		}
		return QRadarAPIUtility.formURLWithParameters(inURL, listOfParams);
	}

	/**
	 *
	 * This method removes json fragments
	 *
	 * @param stringJson
	 * @return
	 */
	private static String unjsonify(String stringJson) {
		stringJson = stringJson.replace("\"", "").replace("{", "").replace("}", "").replace("]", "");
		if (stringJson.startsWith(EVENTS_HEADER)) {
			stringJson = stringJson.replace(EVENTS_HEADER, "");
		}
		return stringJson;
	}

}