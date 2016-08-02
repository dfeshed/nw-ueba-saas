package fortscale.web.rest.Utils;

import fortscale.common.datastructures.UserActivityEntryHashMap;
import fortscale.domain.core.Computer;
import fortscale.domain.core.activities.UserActivityDeviceDocument;
import fortscale.domain.core.activities.UserActivityDocument;
import fortscale.services.ComputerService;
import fortscale.web.rest.entities.activity.UserActivityData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserActivityUtils {

	@Autowired
	private ComputerService computerService;

	@Autowired
	public UserAndOrganizationActivityHelper userAndOrganizationActivityHelper;

	/**
	 * Convert list of UserActivitySourceMachineDocument to list of UserActivityData.DeviceEntry and add device types
	 *
	 * @param documentList
	 * @param limit
	 * @return list of UserActivityData.DeviceEntry
	 */
	public List<UserActivityData.DeviceEntry> convertDeviceDocumentsResponse(
			List<? extends UserActivityDeviceDocument> documentList, Integer limit) {
		final UserActivityEntryHashMap userActivityDataEntries = getUserActivityDataEntries(documentList, userAndOrganizationActivityHelper.getDeviceValuesToFilter());

		final Set<Map.Entry<String, Double>> topEntries = userActivityDataEntries.getTopEntries(limit);
		List<UserActivityData.DeviceEntry> machineEntries = topEntries
				.stream()
				.map(entry -> new UserActivityData.DeviceEntry(entry.getKey(), entry.getValue(), null))
				.collect(Collectors.toList());

		setDeviceType(machineEntries);
		return machineEntries;
	}

	public UserActivityEntryHashMap getUserActivityDataEntries(
			List<? extends UserActivityDocument> userActivityDocumentEntries, Set<String> filteredKeys) {

		UserActivityEntryHashMap currentKeyToCountDictionary = new UserActivityEntryHashMap(filteredKeys);

		//get an aggregated map of 'key' to 'count'
		userActivityDocumentEntries.forEach(userActivityDevice ->
				userActivityDevice.getHistogram().entrySet().stream().forEach(entry ->
						currentKeyToCountDictionary.put(entry.getKey(), entry.getValue())));
		return currentKeyToCountDictionary;
	}

	private void setDeviceType(List<UserActivityData.DeviceEntry> sourceMachineEntries) {
		Set<String> deviceNames = new HashSet<>();
		sourceMachineEntries.forEach(device -> {
			deviceNames.add(device.getDeviceName());
		});
		List<Computer> computers = computerService.findByNameValueIn(deviceNames.toArray(new String[deviceNames.size()]));
		//Create map of computer name to computer OS
		Map<String, String> computerMap = new HashMap<>();
		computers.forEach(computer -> computerMap.put(computer.getName(), computer.getOperatingSystem()));

		//For each device
		sourceMachineEntries.forEach(device -> {
			String name = device.getDeviceName();
			String os = computerMap.get(name);
			//If OS found try to find each device type it contain. If it not contain any of the types, return empty
			if (StringUtils.isNotBlank(os)) {
				for (UserActivityData.DeviceType deviceType : UserActivityData.DeviceType.values()) {
					if (os.toLowerCase().contains(deviceType.name().toLowerCase())) {
						device.setDeviceType(deviceType);
					}
				}
			}
		});
	}
}
