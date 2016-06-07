package fortscale.web.rest;

import fortscale.web.beans.bean.editors.DateRangeEditor;
import fortscale.domain.dto.DateRange;
import fortscale.web.extensions.FortscaleCustomEditorService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    FortscaleCustomEditorService fortscaleCustomEditorService;

    //Define how to convert string into object, using property editor, according to specific attributes
    @InitBinder
    public void dataBinding(WebDataBinder binder) {


        for (FortscaleCustomEditorService.ClassToCustomEditor classToCustomEditor : fortscaleCustomEditorService.getAllCustomEditors()) {
            binder.registerCustomEditor(classToCustomEditor.getaClass(), classToCustomEditor.getPropertyEditor());
        }
    }
}
