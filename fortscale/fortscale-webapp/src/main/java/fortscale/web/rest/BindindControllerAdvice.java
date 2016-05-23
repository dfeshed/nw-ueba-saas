package fortscale.web.rest;

import fortscale.web.beans.bean.editors.DateRangeEditor;
import fortscale.web.beans.request.DateRange;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Created by shays on 23/05/2016.
 */
@ControllerAdvice
public class BindindControllerAdvice {

    @InitBinder
    public void dataBinding(WebDataBinder binder) {


        binder.registerCustomEditor(DateRange.class, new DateRangeEditor());
    }
}
