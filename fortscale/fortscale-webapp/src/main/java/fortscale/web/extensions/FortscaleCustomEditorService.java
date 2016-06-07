package fortscale.web.extensions;

import fortscale.domain.dto.DateRange;
import fortscale.web.beans.bean.editors.DateRangeEditor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 07/06/2016.
 */

@Component
public class FortscaleCustomEditorService {

    //All custom editors must be declared here so DataBinder will use them
    private List<ClassToCustomEditor> allCustomEditors;

    //Only specific attributes that their editor must be declared by name must be here.
    //Be carefull using this one, pay attantion not to use common attribute names
    private Map<String,ClassToCustomEditor> attributeNameToCustomEditor;

    @PostConstruct
    public void init(){

        ClassToCustomEditor dateRangeCustomEditor = new ClassToCustomEditor(DateRange.class, new DateRangeEditor());
        allCustomEditors.add(dateRangeCustomEditor);
        attributeNameToCustomEditor.put("alert_start_range", dateRangeCustomEditor);
    }

    public List<ClassToCustomEditor> getAllCustomEditors() {
        return allCustomEditors;
    }

    public void setAllCustomEditors(List<ClassToCustomEditor> allCustomEditors) {
        this.allCustomEditors = allCustomEditors;
    }

    public Map<String, ClassToCustomEditor> getAttributeNameToCustomEditor() {
        return attributeNameToCustomEditor;
    }

    public void setAttributeNameToCustomEditor(Map<String, ClassToCustomEditor> attributeNameToCustomEditor) {
        this.attributeNameToCustomEditor = attributeNameToCustomEditor;
    }


    public static class ClassToCustomEditor {

        private Class aClass;
        private PropertyEditorSupport propertyEditor;

        public ClassToCustomEditor() {

        }

        public ClassToCustomEditor(Class aClass, PropertyEditorSupport propertyEditor) {
            this.aClass = aClass;
            this.propertyEditor = propertyEditor;
        }


        public Class getaClass() {
            return aClass;
        }

        public void setaClass(Class aClass) {
            this.aClass = aClass;
        }

        public PropertyEditorSupport getPropertyEditor() {
            return propertyEditor;
        }

        public void setPropertyEditor(PropertyEditorSupport propertyEditor) {
            this.propertyEditor = propertyEditor;
        }

    }
}
