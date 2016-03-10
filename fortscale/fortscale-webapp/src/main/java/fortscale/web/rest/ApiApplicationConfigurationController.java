package fortscale.web.rest;

import com.sun.jersey.spi.inject.Errors;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/application_configuration")
public class ApiApplicationConfigurationController extends BaseController {

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    private final String ITEMS_FIELD_NAME = "items";
    private final String ITEMS_KEY_FIELD_NAME = "key";
    private final String ITEMS_VALUE_FIELD_NAME = "value";

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
    @LogException
    public DataBean<List<ApplicationConfiguration>> getConfigurations() {
        DataBean<List<ApplicationConfiguration>> applicationConfigurationDataList = new DataBean<>();
        List<ApplicationConfiguration> applicationConfigurationList = applicationConfigurationService.getApplicationConfiguration();

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
    @LogException
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
                return this.responseErrorHandler("Could not update config items. Items item " + i + " does not have a '" +
                        this.ITEMS_KEY_FIELD_NAME + "' property.", HttpStatus.BAD_REQUEST);
            }

            try {
                value = jsonItems.getJSONObject(i).get(this.ITEMS_VALUE_FIELD_NAME).toString();
            }
            catch (JSONException e) {
                return this.responseErrorHandler("Could not update config items. Items item " + i + " does not have a '" +
                        this.ITEMS_VALUE_FIELD_NAME + "' property.", HttpStatus.BAD_REQUEST);
            }

            configItems.put(key, value);
        }

        // Update config items.
        applicationConfigurationService.updateConfigItems(configItems);

        return new ResponseEntity(HttpStatus.NO_CONTENT);

    }
}
