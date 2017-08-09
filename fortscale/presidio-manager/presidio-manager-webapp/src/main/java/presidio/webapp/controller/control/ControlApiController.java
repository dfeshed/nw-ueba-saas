package presidio.webapp.controller.control;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import presidio.webapp.model.PresidioVersion;
import presidio.webapp.model.UpgradeState;


@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

@Controller
public class ControlApiController implements ControlApi {

    public ResponseEntity<Void> controlUpgradeStartPost() {
        throw new UnsupportedOperationException();
//        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<UpgradeState> controlUpgradeStatusGet() {
        throw new UnsupportedOperationException();
//        return new ResponseEntity<UpgradeState>(HttpStatus.OK);
    }

    public ResponseEntity<PresidioVersion> controlVersionGet() {
        throw new UnsupportedOperationException();
//        return new ResponseEntity<PresidioVersion>(HttpStatus.OK);
    }

}
