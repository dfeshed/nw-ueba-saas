package presidio.ui.presidiouiapp.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.EncryptionUtils;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;

import fortscale.utils.logging.annotation.LogSensitiveFunctionsAsEnum;
import presidio.ui.presidiouiapp.BaseController;
import presidio.ui.presidiouiapp.beans.DataBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/api/application_configuration")
public class ApiApplicationConfigurationController extends BaseController {


    private ApplicationConfigurationService applicationConfigurationService;

    private final String ITEMS_FIELD_NAME = "items";
    private final String ITEMS_KEY_FIELD_NAME = "key";
    private final String ITEMS_VALUE_FIELD_NAME = "value";
    private final String ITEMS_META_FIELD_NAME = "meta";
    private final String META_ENCRYPT = "encrypt";
    private final String META_FIELDS = "fields";
	private final String ENCRYPTION_DONE_PLACEHOLDER = "ENCRYPTION_DONE";

    public ApiApplicationConfigurationController(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    /**
     * Handles response errors.
     *
     * @param message The message to be returned.
     * @param status The status to be returned.
     * @return ResponseEntity<String>
     */
    private ResponseEntity<String> responseErrorHandler(String message, HttpStatus status) {
        JSONObject errorBody = new JSONObject();
        errorBody.put("message", message);
        return new ResponseEntity<>(errorBody.toString(), status);
    }

    /**
     * The API to get all users. GET: /api/application_configuration
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
//    @LogException
    public DataBean<List<ApplicationConfiguration>> getConfigurations(String[] namespace) {
        DataBean<List<ApplicationConfiguration>> applicationConfigurationDataList = new DataBean<>();
        List<ApplicationConfiguration> applicationConfigurationList = null;
        if (namespace == null  || namespace.length==0) {
            applicationConfigurationList = applicationConfigurationService.getApplicationConfiguration();
        } else {
            Set<ApplicationConfiguration> applicationConfigurationSet = new HashSet<>();
            for (String singleNamesspace : namespace) {
                applicationConfigurationSet.addAll(applicationConfigurationService.
						getApplicationConfigurationAsListByNamespace(singleNamesspace));
            }
            applicationConfigurationList= new ArrayList<>(applicationConfigurationSet);
        }
        applicationConfigurationDataList.setData(applicationConfigurationList);
        applicationConfigurationDataList.setTotal(applicationConfigurationList.size());
        return applicationConfigurationDataList;
    }

    /**
     * Updates or creates config items.
     *
     * @param body Accepted POST body Should be {items: Array<{key: string, value: string}>}
     * @return ResponseEntity
     * @throws JSONException
     */
    @RequestMapping(method = RequestMethod.POST)
    @HideSensitiveArgumentsFromLog(sensitivityCondition = LogSensitiveFunctionsAsEnum.APPLICATION_CONFIGURATION)
//    @LogException
    public ResponseEntity updateConfigItems(@RequestBody String body) throws JSONException {
        // Parse json. Return BAD_REQUEST If can not parse
        JSONObject params;
        try {
            params = new JSONObject(body);
        } catch (JSONException e) {
            return this.responseErrorHandler("Could not update config items. Failed to parse POST Body to JSON.",
                    HttpStatus.BAD_REQUEST);
        }
        // Get "Items" from body. Return BAD_REQUEST if items does not exist or is invalid.
        JSONArray jsonItems;
        try {
            jsonItems = params.getJSONArray(this.ITEMS_FIELD_NAME);
        } catch (JSONException e) {
            return this.responseErrorHandler("Could not update config items. POST Body did not have a valid 'items' list.",
                    HttpStatus.BAD_REQUEST);
        }
        // Create configItems Map. Iterate through jsonItems and for each one get key and value and store in map.
        Map<String, String> configItems = new HashMap<>();
        for (int i = 0; i < jsonItems.length(); i++) {
            String key;
            String value;
            try {
                key = jsonItems.getJSONObject(i).getString(this.ITEMS_KEY_FIELD_NAME);
            }
            catch (JSONException e) {
                return this.responseErrorHandler("Could not update config items. Items item " + i +
						" does not have a '" + this.ITEMS_KEY_FIELD_NAME + "' property.", HttpStatus.BAD_REQUEST);
            }
            try {
                value = jsonItems.getJSONObject(i).get(this.ITEMS_VALUE_FIELD_NAME).toString();
            }
            catch (JSONException e) {
                return this.responseErrorHandler("Could not update config items. Items item " + i +
						" does not have a '" + this.ITEMS_VALUE_FIELD_NAME + "' property.", HttpStatus.BAD_REQUEST);
            }
            if (jsonItems.getJSONObject(i).has(ITEMS_META_FIELD_NAME)) {
                JSONObject meta = jsonItems.getJSONObject(i).getJSONObject(ITEMS_META_FIELD_NAME);
                if (meta.has(META_ENCRYPT) && meta.getBoolean(META_ENCRYPT)) {
                    if (meta.has(META_FIELDS)) {
                        JSONArray fields = meta.getJSONArray(META_FIELDS);
						Pattern base64Pattern = Pattern.compile("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})$");
                        for (int j = 0; j < fields.length(); j++) {
                            String field = fields.getString(j);
                            Pattern pattern = Pattern.compile("(?<=\"" + field + "\":\").+?(?=\")");
							while (value.indexOf("\"" + field + "\":") > -1) {
								Matcher matcher = pattern.matcher(value);
								if (matcher.find()) {
									String innerValue = matcher.group(0).trim();
									//this is to avoid double encryption
									Matcher base64Matcher = base64Pattern.matcher(innerValue);
									//if not base64 encoded
									if (!base64Matcher.find()) {
										try {
											value = value.replaceFirst("(?<=\"" + field + "\":\").+?(?=\")",
													EncryptionUtils.encrypt(innerValue).trim());
										} catch (Exception ex) {
											return this.responseErrorHandler("Could not encrypt config items",
													HttpStatus.BAD_REQUEST);
										}
									}
									value = value.replaceFirst("\"" + field + "\":", ENCRYPTION_DONE_PLACEHOLDER);
								}
							}
							value = value.replaceAll(ENCRYPTION_DONE_PLACEHOLDER, "\"" + field + "\":");
                        }
                    } else {
						try {
							value = EncryptionUtils.encrypt(value).trim();
						} catch (Exception ex) {
							return this.responseErrorHandler("Could not encrypt config items",
									HttpStatus.BAD_REQUEST);
						}
                    }
                }
            }
            configItems.put(key, value);
        }
        // Update config items.
        applicationConfigurationService.updateConfigItems(configItems);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}