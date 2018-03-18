package fortscale.web.extensions;

import com.google.common.base.CaseFormat;
import fortscale.web.beans.bean.editors.DateRangeEditor;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServletRequestDataBinder which supports fields renaming to underscore case to camel case
 *
 * @author shays
 */
public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {


    private  FortscaleCustomEditorService fortscaleCustomEditorService;

    public ParamNameDataBinder(Object target) {
        super(target);
    }

    public ParamNameDataBinder(Object target, String objectName, FortscaleCustomEditorService fortscaleCustomEditorService) {
        super(target, objectName);
        this.fortscaleCustomEditorService = fortscaleCustomEditorService;
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {

        super.addBindValues(mpvs, request);

        Map<String, Object> newNames = new HashMap<>();

        for (PropertyValue nameValue: mpvs.getPropertyValueList()){

            String underscoreKey = nameValue.getName();
            if (underscoreKey.contains("_")) {
                String camelCaseKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, underscoreKey);
                
                newNames.put(camelCaseKey, nameValue.getValue());
            }
        }
        mpvs.addPropertyValues(newNames);

        resolveCustomBinders(mpvs);

    }

    private void resolveCustomBinders(MutablePropertyValues mpvs){

        //Run over all properties from web
        //And look if the target object (the final object) has an property with matching name and type the need converstion
        //If we find property on the target object the need conversation we convert and put the converted value on the property
        for (PropertyValue property: mpvs.getPropertyValueList()) {
            try {
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(this.getTarget(), property.getName());
                if (pd != null) {
                    Class propertyClass = pd.getPropertyType();

                    PropertyEditorSupport specificEditor= fortscaleCustomEditorService.getClassToCustomEditor().get(propertyClass);

                    if (null != specificEditor) {
                        Object oldValue = property.getValue();
                        if (oldValue instanceof String) {
                            specificEditor.setAsText((String) oldValue);
                            Object propertyConvertedValue = specificEditor.getValue();
                            property.setConvertedValue(propertyConvertedValue);


                        }
                    }
                }
            } catch (Exception e ){
                //Do nothing
            }

        }

    }
}