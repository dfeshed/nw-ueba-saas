package fortscale.web.extensions;

import com.google.common.base.CaseFormat;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServletRequestDataBinder which supports fields renaming to underscore case to camel case
 *
 * @author jkee
 */
public class ParamNameDataBinder extends ExtendedServletRequestDataBinder {


    public ParamNameDataBinder(Object target) {
        super(target);
    }

    public ParamNameDataBinder(Object target, String objectName) {
        super(target, objectName);
    }

    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {

        super.addBindValues(mpvs, request);
       // print("before inside",mpvs);
        Map<String, Object> newNames = new HashMap<>();

        for (PropertyValue nameValue: mpvs.getPropertyValueList()){

            String underscoreKey = nameValue.getName();
            String camelCaseKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,underscoreKey);

            newNames.put(camelCaseKey,nameValue.getValue());
        }
        mpvs.addPropertyValues(newNames);

//        for (Map.Entry<String, Object> propertyValues : oldValues.entrySet()) {
//            String underscoreKey = propertyValues.getKey();
//            String camelCaseKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,underscoreKey );
//         //   if (mpvs.contains(underscoreKey) && !mpvs.contains(camelCaseKey)) {
//                mpvs.add(propertyValues.getKey(), camelCaseKey);
//
//           // }
//        }
       // print("after inside",mpvs);


    }

//    @Override
//    public void bind(ServletRequest request) {
//        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
//        MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
//        if (multipartRequest != null) {
//            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
//        }
//        print("before outside",mpvs);
//        addBindValues(mpvs, request);
//        print("after outside",mpvs);
//        doBind(mpvs);
//    }
//
//    private void print(String step, MutablePropertyValues mpvs){
//        String message = "Printing mpvs step: "+step+", Data: "+mpvs.toString();
//
//        System.out.println(message);
//    }
}