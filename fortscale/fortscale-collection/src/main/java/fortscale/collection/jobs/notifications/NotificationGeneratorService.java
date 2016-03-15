package fortscale.collection.jobs.notifications;

import net.minidev.json.JSONObject;

import java.util.List;

/**
 * Created by shays on 14/03/2016.
 */
public interface NotificationGeneratorService {

    /**
     *
     * @return boolean, true is completed successfuly, or false if some error took place
     * @throws Exception
     */
    boolean generateNotification () throws Exception;

}
