package fortscale.web.rest;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by avivs on 06/03/16.
 */

@Controller
@RequestMapping("/api/computer")
public class ApiComputerController extends BaseController {

    @Autowired
    private ComputerRepository computerRepository;


    /**
     * Handles response errors.
     *
     * @param message The message to be returned.
     * @param status  The status to be returned.
     * @return ResponseEntity<String>
     */
    private ResponseEntity<String> responseErrorHandler(String message, HttpStatus status) {
        JSONObject errorBody = new JSONObject();
        errorBody.put("message", message);
        return new ResponseEntity<>(errorBody.toString(), status);
    }

    /**
     * Takes a list of computers and returns ResponseEntity with required fields only.
     *
     * @param computers
     * @param fields
     * @return
     */
    private ResponseEntity<List<Map<String, Object>>> returnByFields(List<Computer> computers, String fields) {
        List<Map<String, Object>> list = new ArrayList<>();

        // Iterate through found computers
        computers.forEach(computer -> {

            // Create a new Map
            Map<String, Object> mappedComputer = new HashMap<>();

            // Iterate through required fields
            Arrays.asList(fields.split(",")).forEach(field -> {

                // Iterate through Computer methods to find the method correlating to the required field
                for (Method method : Computer.class.getMethods()) {

                    // Find if field matches
                    String methodName = method.getName();
                    if ((methodName.startsWith("get")) && (methodName.length() == (field.length() + 3)) && methodName.toLowerCase().endsWith(field.toLowerCase())) {
                        try {
                            mappedComputer.put(field, method.invoke(computer));
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            if(mappedComputer.size() > 0 ) {
                list.add(mappedComputer);
            }

        });
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * The API to get all users. GET: /api/computer
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public ResponseEntity getConfigurations(
            @RequestParam(required = false, value = "name_contains") String nameContains,
            @RequestParam(required = false, value = "distinguished_name_contains") String distinguishedNameContains,
            @RequestParam(required = false, value = "fields") String fields,
            @RequestParam(required = false, value = "usage_types") String usageTypes,
            @RequestParam(required = false, value = "usage_types_and") String usageTypesAnd,
            @RequestParam(required = false, value = "limit") Integer limit) {

        List<Computer> computers = computerRepository.findByFilters(nameContains, distinguishedNameContains, fields, usageTypes,
                usageTypesAnd, limit);

        if (fields != null) {
            return returnByFields(computers, fields);
        }

        return new ResponseEntity(computers, HttpStatus.OK);

    }

}

