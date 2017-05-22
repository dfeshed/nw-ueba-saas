package fortscale.controllers;

import fortscale.dto.Alert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shays on 21/05/2017.
 */

@RestController
public class AlertsController {

    @RequestMapping(value = "/alert",method = RequestMethod.GET)
    Alert alert(){
        Alert a= new Alert();
        a.setDescription("Alert Description");
        a.setId("00000001");
        return  a;
        
    }
}
