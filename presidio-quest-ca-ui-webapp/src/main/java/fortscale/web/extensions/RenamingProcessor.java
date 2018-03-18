package fortscale.web.extensions;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by shays on 03/05/2016.
 */
public class RenamingProcessor  extends ServletModelAttributeMethodProcessor implements ApplicationContextAware{


    private ApplicationContext applicationContext;
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

        ParamNameDataBinder paramNameDataBinder = new ParamNameDataBinder(target, binder.getObjectName(), fortscaleCustomEditorService);
        extractRuestMappingHandlerAdapter().getWebBindingInitializer().initBinder(paramNameDataBinder, nativeWebRequest);
        super.bindRequestParameters(paramNameDataBinder, nativeWebRequest);
    }

    /**
     *  I know that using application context is not best practice, but in this case,
     *  I can't get RequestMappingHandlerAdapter while initilizing RenamingProcessor (due to cycling loading) and I need to
     *  fatch it after it was completed
      * @return
     */
    private RequestMappingHandlerAdapter extractRuestMappingHandlerAdapter() {
        if (this.requestMappingHandlerAdapter == null){
            this.requestMappingHandlerAdapter = applicationContext.getBean(RequestMappingHandlerAdapter.class);
        }
        return this.requestMappingHandlerAdapter;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        this.applicationContext = applicationContext;
    }
}
