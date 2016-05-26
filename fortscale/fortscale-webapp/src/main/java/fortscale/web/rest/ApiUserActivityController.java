package fortscale.web.rest;

import fortscale.aggregation.useractivity.services.UserActivityService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * User Activity controller
 *
 * @author gils
 * 22/05/2016
 */
@Controller
@RequestMapping("/api/user/{id}/activity")
public class ApiUserActivityController extends DataQueryController {

    private static final String DEFAULT_TIME_RANGE = "30";
    private static final String DEFAULT_RETURN_ENTRIES_LIMIT = "3";
    private final UserActivityService userActivityService;

    @Autowired
    public ApiUserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    @RequestMapping(value="/locations", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.LocationEntry>> getLocations(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                       @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.LocationEntry>> userActivityLocationsBean = new DataBean<>();

        userActivityService.getLocationEntries();

        List<UserActivityData.LocationEntry> locationEntries = new ArrayList<>();

        locationEntries.add(new UserActivityData.LocationEntry("Israel", 300));
        locationEntries.add(new UserActivityData.LocationEntry("Japan", 2));
        locationEntries.add(new UserActivityData.LocationEntry("USA", 180));
        locationEntries.add(new UserActivityData.LocationEntry("Others", 100));

        userActivityLocationsBean.setData(locationEntries);

        return userActivityLocationsBean;
    }

    @RequestMapping(value="/source-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.SourceDeviceEntry>> getSourceDevices(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                        @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.SourceDeviceEntry>> userActivitySourceDevicesBean = new DataBean<>();

        List<UserActivityData.SourceDeviceEntry> sourceDeviceEntries = new ArrayList<>();

        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("SRV_150", 500, UserActivityData.DeviceType.Server));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("MOBILE_123", 100, UserActivityData.DeviceType.Mobile));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("GILS_PC1", 1000, UserActivityData.DeviceType.Desktop));
        sourceDeviceEntries.add(new UserActivityData.SourceDeviceEntry("Others", 2000, UserActivityData.DeviceType.Desktop));

        userActivitySourceDevicesBean.setData(sourceDeviceEntries);

        return userActivitySourceDevicesBean;
    }

    @RequestMapping(value="/target-devices", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.TargetDeviceEntry>> getTargetDevices(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays,
                                                                               @RequestParam(required = false, defaultValue = DEFAULT_RETURN_ENTRIES_LIMIT, value = "limit") Integer limit){
        DataBean<List<UserActivityData.TargetDeviceEntry>> userActivityTargetDevicesBean = new DataBean<>();

        List<UserActivityData.TargetDeviceEntry> sourceDeviceEntries = new ArrayList<>();

        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("SRV_150", 500));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("MOBILE_123", 100));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("GILS_PC1", 1000));
        sourceDeviceEntries.add(new UserActivityData.TargetDeviceEntry("Others", 3000));

        userActivityTargetDevicesBean.setData(sourceDeviceEntries);

        return userActivityTargetDevicesBean;
    }

    @RequestMapping(value="/authentications", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.Authentications>> getAuthentications(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.Authentications>> userActivityAuthenticationsBean = new DataBean<>();

        List<UserActivityData.Authentications> authentications = new ArrayList<>();

        authentications.add(new UserActivityData.Authentications(5000, 200));

        userActivityAuthenticationsBean.setData(authentications);

        return userActivityAuthenticationsBean;
    }

    @RequestMapping(value="/data-usage", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.DataUsageEntry>> getDataUsage(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.DataUsageEntry>> userActivityDataUsageBean = new DataBean<>();

        List<UserActivityData.DataUsageEntry> dataUsages = new ArrayList<>();

        dataUsages.add(new UserActivityData.DataUsageEntry("vpn", 300, "MBs"));
        dataUsages.add(new UserActivityData.DataUsageEntry("ssh", 5000, "MBs"));
        dataUsages.add(new UserActivityData.DataUsageEntry("oracle", 100, "MBs"));

        userActivityDataUsageBean.setData(dataUsages);

        return userActivityDataUsageBean;
    }

    @RequestMapping(value="/working-hours", method= RequestMethod.GET)
    @ResponseBody
    @LogException
    public DataBean<List<UserActivityData.WorkingHourEntry>> getWorkingHours(@RequestParam(required = false, defaultValue = DEFAULT_TIME_RANGE, value = "time_range") Integer timePeriodInDays){
        DataBean<List<UserActivityData.WorkingHourEntry>> userActivityWorkingHoursBean = new DataBean<>();

        List<UserActivityData.WorkingHourEntry> workingHours = new ArrayList<>();

        workingHours.add(new UserActivityData.WorkingHourEntry(8));
        workingHours.add(new UserActivityData.WorkingHourEntry(9));
        workingHours.add(new UserActivityData.WorkingHourEntry(10));
        workingHours.add(new UserActivityData.WorkingHourEntry(11));
        workingHours.add(new UserActivityData.WorkingHourEntry(12));
        workingHours.add(new UserActivityData.WorkingHourEntry(13));

        userActivityWorkingHoursBean.setData(workingHours);

        return userActivityWorkingHoursBean;
    }
}