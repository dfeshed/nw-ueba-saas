package fortscale.web.rest;

import fortscale.web.beans.bean.editors.DateRangeEditor;
import fortscale.domain.dto.DateRange;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Created by shays on 23/05/2016.
 * This advice controller used to define items which common to all controllers.
 *
 *
 */
@ControllerAdvice
public class BindindControllerAdvice {

    //Define how to convert string into object, using property editor
    @InitBinder
    public void dataBinding(WebDataBinder binder) {


        //Convert any data range from string to DateRange using DateRangeEditor
        binder.registerCustomEditor(DateRange.class, new DateRangeEditor());
    }
}
