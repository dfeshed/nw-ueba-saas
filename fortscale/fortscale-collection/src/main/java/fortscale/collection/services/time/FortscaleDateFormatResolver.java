package fortscale.collection.services.time;

import org.springframework.beans.factory.InitializingBean;

import java.util.LinkedList;

/**
 * @author gils
 * 03/03/2016
 */
public class FortscaleDateFormatResolver implements InitializingBean{
    @Override
    public void afterPropertiesSet() throws Exception {
        LinkedList<String> availableInputFormats = FortscaleTimeFormats.getAvailableInputFormats();
        String x = null;
    }
}
