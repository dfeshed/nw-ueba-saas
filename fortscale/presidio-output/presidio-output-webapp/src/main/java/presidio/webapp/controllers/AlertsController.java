package presidio.webapp.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import presidio.webapp.dto.Alert;

/**
 * Created by shays on 21/05/2017.
 */


@RestController
public class AlertsController {

    @Value("${shay.shay}")
    private String name;

    @RequestMapping(value = "/alert",method = RequestMethod.GET)
    Alert alert(){
        Alert a= new Alert();
        a.setDescription(name);
        a.setId("00000001");
        return  a;
        
    }
}
