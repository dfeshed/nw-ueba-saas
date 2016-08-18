package fortscale.collection.jobs.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gils
 * 31/05/2016
 */
@Component
public class UserActivityHandlerFactory {

    private final List<UserActivityHandler> allActivityHandlers;

    @Autowired
    public UserActivityHandlerFactory(List<UserActivityHandler> allActivityHandlers) {
        this.allActivityHandlers = allActivityHandlers;
    }

    public UserActivityHandler createUserActivityHandler(String activityName) {
        UserActivityType activityType;
        try {
            activityType = UserActivityType.valueOf(activityName.toUpperCase());
        } catch (Exception e){
            throw new UnsupportedOperationException("Could not find activity of type " + activityName);
        }
        for (UserActivityHandler userActivityHandler : allActivityHandlers){
            if (userActivityHandler.getActivity().equals(activityType)){
                return userActivityHandler;
            }
        }
        throw new UnsupportedOperationException("Could not find activity of type " + activityName);
    }

}