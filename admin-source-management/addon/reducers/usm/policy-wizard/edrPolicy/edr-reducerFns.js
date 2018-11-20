import { handle } from 'redux-pack';

const scanScheduleId = 'scanType';

// ACTION_TYPES.FETCH_ENDPOINT_SERVERS
const fetchEndpointServers = (state, action) => (
  handle(state, action, {
    start: (state) => {
      return state.set('listOfEndpointServers', []);
    },
    success: (state) => {
      return state.set('listOfEndpointServers', action.payload.data);
    }
  })
);

// define-policy-step -
// ACTION_TYPES.TOGGLE_SCAN_TYPE
const toggleScanType = (state, { payload }) => {
  const { availableSettings, selectedSettings } = state;
  if (payload === 'ENABLED') {
    const newAvailableSettings = availableSettings.map((el) => {
      // if any of the objects in the array is the child of scanSchedule they should be lit up
      if (el.parentId === scanScheduleId) {
        return {
          ...el,
          isGreyedOut: false
        };
      }
      return el;
    });
    return state.merge({
      policy: {
        scanType: payload
      },
      availableSettings: newAvailableSettings
    }, { deep: true }); // deep merge so we don't reset everything
  } else { // 'DISABLED'
    const newAvailableSettings = availableSettings.map((el) => {
      // if any of the objects in the array is the child of scanSchedule they should be greyed out
      if (el.parentId === scanScheduleId) {
        return {
          ...el,
          isGreyedOut: true,
          isEnabled: true
        };
      }
      return el;
    });
    return state.merge({
      policy: {
        scanType: payload,
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: null,
        recurrenceUnit: null,
        runOnDaysOfWeek: null,
        cpuMax: null,
        cpuMaxVm: null
      },
      availableSettings: newAvailableSettings,
      selectedSettings: selectedSettings.filter((el) => el.parentId !== scanScheduleId)
    }, { deep: true }); // deep merge so we don't reset everything
  }
};

// define-policy-step - When RESET_SCAN_SCHEDULE is dispatched, everything under Scan Schedule
// should be set to default state and moved to the left. Other selected settings will remain as it is.
// ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS
const resetScanScheduleToDefaults = (state) => {
  const { availableSettings, selectedSettings } = state;

  // remove scan schedule and it's children from the selected settings if present
  const newSelectedSettings = selectedSettings.filter((el) => {
    if (el.parentId === scanScheduleId || el.id === scanScheduleId) {
      return false;
    }
    return true;
  });
  // set scan schedule to default values in available settings (children greyed out and enabled)
  const newAvailableSettings = availableSettings.map((el) => {
    if (el.parentId === scanScheduleId) {
      return {
        ...el,
        isEnabled: true,
        isGreyedOut: true
      };
    }
    if (el.id === scanScheduleId) {
      return {
        ...el,
        isEnabled: true,
        isGreyedOut: false
      };
    }
    return el;
  });
  // when scan schedule is removed from selected settings, all it's child components
  // (effective date, recurrence interval, processor usage) should be removed.
  // so reset the scheduleConfig & available/selected settings to defaults
  return state.merge({
    policy: {
      scanType: null,
      scanStartDate: null,
      scanStartTime: null,
      recurrenceInterval: null,
      recurrenceUnit: null,
      runOnDaysOfWeek: null,
      cpuMax: null,
      cpuMaxVm: null
    },
    availableSettings: newAvailableSettings,
    selectedSettings: newSelectedSettings
  }, { deep: true }); // deep merge so we don't reset everything
};

export default {
  fetchEndpointServers,
  toggleScanType,
  resetScanScheduleToDefaults
};
