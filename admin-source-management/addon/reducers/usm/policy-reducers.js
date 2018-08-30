import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import moment from 'moment';
import _ from 'lodash';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const initialState = {
  policy: {
    name: '',
    description: '',
    scheduleConfig: {
      scanType: 'MANUAL',
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null,
        scanStartTime: '10:00',
        recurrenceInterval: 5,
        recurrenceIntervalUnit: 'DAYS',
        runOnDaysOfWeek: []
      },
      scanOptions: {
        cpuMaximum: 75,
        cpuMaximumOnVirtualMachine: 85
      }
    }
  },
  policyStatus: null, // wait, complete, error
  availableSettings: [
    { index: 0, id: 'schedOrManScan', label: 'Scheduled or Manual Scan', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 1, id: 'effectiveDate', label: 'Effective Date', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/effective-date' },
    { index: 2, id: 'recurrenceInterval', label: 'Scan Frequency', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/recurrence-interval' },
    { index: 3, id: 'startTime', label: 'Start Time', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/start-time' },
    { index: 4, id: 'cpuMax', label: 'CPU Maximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/cpu-max' },
    { index: 5, id: 'vmMax', label: 'Virtual Machine Maximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/vm-max' }
  ],
  selectedSettings: []
};

const scanScheduleId = 'schedOrManScan';

export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_POLICY]: (state) => {
    const newState = state.merge({
      policy: { ...initialState.policy },
      policyStatus: null
    });
    const fields = 'policy.scheduleConfig.scheduleOptions.scanStartDate'.split('.');
    const scanStartDateToday = moment().startOf('date').toDate().getTime();
    return newState.setIn(fields, scanStartDateToday);
  },

  [ACTION_TYPES.EDIT_POLICY]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    return state.setIn(fields, value);
  },

  [ACTION_TYPES.TOGGLE_SCAN_TYPE]: (state, { payload }) => {
    const { availableSettings, selectedSettings } = state;
    // TODO Flatten out the initialState so that we don't have to deal with split and setIn
    const scanType = 'policy.scheduleConfig.scanType'.split('.');

    if (payload === 'SCHEDULED') {
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
      const newState = state.merge({
        policy: { ...initialState.policy },
        availableSettings: newAvailableSettings
      });
      return newState.setIn(scanType, payload);
    } else { // 'MANUAL'
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
      const newState = state.merge({
        policy: { ...initialState.policy },
        availableSettings: newAvailableSettings,
        selectedSettings: selectedSettings.filter((el) => el.parentId !== scanScheduleId)
      });
      const scheduleOptions = 'policy.scheduleConfig.scheduleOptions'.split('.');
      const scanOptions = 'policy.scheduleConfig.scanOptions'.split('.');
      return newState.setIn(scanType, payload).setIn(scheduleOptions, null).setIn(scanOptions, null);
    }
  },

  [ACTION_TYPES.ADD_TO_SELECTED_SETTINGS]: (state, { payload }) => {
    const id = payload;
    const { selectedSettings, availableSettings } = state;

    const newSelectedSettings = availableSettings.find((d) => d.id === id);
    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === id) {
        return {
          ...el,
          isEnabled: false
        };
      }
      // if the scan type is "SCHEDULED" in state, nothing should be greyed out
      // in availableSettings
      if (state.policy.scheduleConfig.scanType === 'SCHEDULED') {
        return {
          ...el,
          isGreyedOut: false
        };
      }
      return el;
    });
    return state.merge({
      availableSettings: newAvailableSettings,
      selectedSettings: _.uniqBy([ ...selectedSettings, newSelectedSettings ], 'id')
    });
  },

  [ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS]: (state, { payload }) => {
    const id = payload;
    const { selectedSettings, availableSettings } = state;

    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === id) {
        return {
          ...el,
          isEnabled: true
        };
      }
      return el;
    });
    return state.merge({
      availableSettings: newAvailableSettings,
      selectedSettings: selectedSettings.filter((el) => el.id !== id)
    });
  },

  [ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS]: (state) => {
    // when scan schedule is removed from selected settings, all it's child components
    // (effective date, recurrence interval, processor usage) should be removed.
    // so reset the state to defaults
    return state.merge({
      ...initialState
    });
  },

  [ACTION_TYPES.UPDATE_POLICY_PROPERTY]: (state, action) => state.merge({ policy: action.payload }, { deep: true }),

  [ACTION_TYPES.SAVE_POLICY]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('policyStatus', 'wait');
      },
      failure: (state) => {
        return state.set('policyStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policy: action.payload.data,
          policyStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(initialState));
