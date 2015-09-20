package fortscale.services;

import java.util.Date;

/**
 * Created by amirk on 20/09/15.
 */
public interface Service {

    long deleteBetween(Date start, Date end);

}