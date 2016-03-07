package fortscale.web.rest;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
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
     * Takes a list of computers and returns ResponseEntity with required fields only.
     *
     * @param computers
     * @param fields
     * @return
     */
    private ResponseEntity<DataBean> returnByFields(List<Computer> computers, String fields) {
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
                    if ((methodName.startsWith("get")) && (methodName.length() == (field.length() + 3)) &&
                            methodName.toLowerCase().endsWith(field.toLowerCase())) {
                        try {
                            mappedComputer.put(field, method.invoke(computer));
                        } catch (InvocationTargetException|IllegalAccessException e) {
                            throw new Error("There was a problem with the requested field: " + field);
                        }
                    }
                }
            });

            if(mappedComputer.size() > 0 ) {
                list.add(mappedComputer);
            }

        });

        // Create DataBean
        DataBean<List<Map<String, Object>>> computersBean = new DataBean<>();
        computersBean.setData(list);
        return new ResponseEntity<>(computersBean, HttpStatus.OK);
    }

    /**
     * The API to get all computers or filtered computers. GET: /api/computer
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @LogException
    public ResponseEntity<DataBean> getComputers(
            @RequestParam(required = false, value = "name_contains") String nameContains,
            @RequestParam(required = false, value = "distinguished_name_contains") String distinguishedNameContains,
            @RequestParam(required = false, value = "usage_types") String usageTypes,
            @RequestParam(required = false, value = "usage_types_and") String usageTypesAnd,
            @RequestParam(required = false, value = "limit") Integer limit,
            @RequestParam(required = false, value = "fields") String fields) {

        List<Computer> computers = computerRepository.findByFilters(nameContains, distinguishedNameContains, usageTypes,
                usageTypesAnd, limit, fields);

        if (fields != null) {
            return returnByFields(computers, fields);
        }

        // Create DataBean
        DataBean<List<Computer>> computersBean = new DataBean<>();
        computersBean.setData(computers);

        return new ResponseEntity<>(computersBean, HttpStatus.OK);

    }

}

