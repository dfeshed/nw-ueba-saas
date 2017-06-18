package fortscale.utils.cleanup;

import java.util.Date;

/**
 * Created by Amir Keren on 22/09/15.
 */
public interface CleanupUtil {

    boolean deleteEntityBetween(String toDelete, String queryField, Date startDate, Date endDate);

}