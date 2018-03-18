package fortscale.web.rest;

import fortscale.web.beans.bean.editors.DateRangeEditor;
import fortscale.domain.dto.DateRange;
import fortscale.web.extensions.FortscaleCustomEditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Map;

/**
 * Created by shays on 23/05/2016.
 * This advice controller used to define items which common to all controllers.
 *
 *
 */
@ControllerAdvice
public class BindindControllerAdvice {

    @Autowired
    FortscaleCustomEditorService fortscaleCustomEditorService;

    //Define how to convert string into object, using property editor, according to specific attributes
    @InitBinder
    public void dataBinding(WebDataBinder binder) {



        for (Map.Entry<Class,PropertyEditorSupport> classToCustomEditor : fortscaleCustomEditorService.getClassToCustomEditor().entrySet()) {
            binder.registerCustomEditor(classToCustomEditor.getKey(),classToCustomEditor.getValue());
        }
    }
}
