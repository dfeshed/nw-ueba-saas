package fortscale.web.extensions;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shays on 03/05/2016.
 */
public class RenamingProcessor  extends ServletModelAttributeMethodProcessor {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private  FortscaleCustomEditorService fortscaleCustomEditorService;

    //Rename cache
    private final Map<Class<?>, Map<String, String>> replaceMap = new ConcurrentHashMap<Class<?>, Map<String, String>>();

    public RenamingProcessor(boolean annotationNotRequired) {
        super(annotationNotRequired);
    }

    @Override
    protected void bindRequestParameters(WebDataBinder binder, NativeWebRequest nativeWebRequest) {
        Object target = binder.getTarget();
        Class<?> targetClass = target.getClass();

        ParamNameDataBinder paramNameDataBinder = new ParamNameDataBinder(target, binder.getObjectName(), fortscaleCustomEditorService);
        requestMappingHandlerAdapter.getWebBindingInitializer().initBinder(paramNameDataBinder, nativeWebRequest);
        super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    }



}
