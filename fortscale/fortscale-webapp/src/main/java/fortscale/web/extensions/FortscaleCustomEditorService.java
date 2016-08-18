package fortscale.web.extensions;

import fortscale.domain.dto.DateRange;
import fortscale.web.beans.bean.editors.DateRangeEditor;
import fortscale.domain.rest.AlertRestFilter;
import fortscale.domain.rest.DataSourceAnomalyTypePairListWrapperPropertyEditor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.beans.PropertyEditorSupport;
import java.util.HashMap;

import java.util.Map;

/**
 * Created by shays on 07/06/2016.
 */

@Component
public class FortscaleCustomEditorService {



    Map<Class,PropertyEditorSupport> classToCustomEditor = new HashMap<>();


    @PostConstruct
    public void init(){
        //Register DateRange custom editor
        classToCustomEditor.put(DateRange.class, new DateRangeEditor());

        //Register DataSourceAnomalyTypePairListWrapperPropertyEditor custom editor
        DataSourceAnomalyTypePairListWrapperPropertyEditor dataSourceAnomalyTypePairListWrapperPropertyEditor = new DataSourceAnomalyTypePairListWrapperPropertyEditor();
        classToCustomEditor.put(AlertRestFilter.DataSourceAnomalyTypePairListWrapper.class, dataSourceAnomalyTypePairListWrapperPropertyEditor);

    }

    public Map<Class, PropertyEditorSupport> getClassToCustomEditor() {
        return classToCustomEditor;
    }

}
