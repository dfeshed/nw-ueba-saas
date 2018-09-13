import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import moment from 'moment';
import _ from 'lodash';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

export const initialState = {
  // the policy object to be created/updated/saved
  policy: {
    id: null,
    name: '',
    description: '',
    dirty: true,
    lastPublishedCopy: null,
    lastPublishedOn: 0,
    policyType: 'edrPolicy',
    scheduleConfig: {
      scanType: 'MANUAL',
      enabledScheduledScan: false,
      scheduleOptions: {
        scanStartDate: null, // YYYY-MM-DD
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

  // TODO if the reducer doesn't need to modify these, and the selectors aren't doing anything special,
  //   then we may want to extract these to a steps.js and add them directly to the policy-wizard component,
  //   but keep in mind that we may want to dynamically add steps...
  steps: [
    {
      id: 'identifyPolicyStep',
      nextStepId: 'definePolicyStep',
      prevStepId: '',
      title: 'adminUsm.policyWizard.identifyPolicy',
      stepComponent: 'usm-policies/policy-wizard/identify-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'definePolicyStep',
      prevStepId: 'identifyPolicyStep',
      nextStepId: 'applyToGroupStep',
      title: 'adminUsm.policyWizard.definePolicy',
      stepComponent: 'usm-policies/policy-wizard/define-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'applyToGroupStep',
      prevStepId: 'definePolicyStep',
      nextStepId: 'reviewPolicyStep',
      title: 'adminUsm.policyWizard.applyToGroup',
      stepComponent: 'usm-policies/policy-wizard/apply-to-group-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'reviewPolicyStep',
      prevStepId: 'applyToGroupStep',
      nextStepId: '',
      title: 'adminUsm.policyWizard.reviewPolicy',
      stepComponent: 'usm-policies/policy-wizard/review-policy-step',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    }
  ],

  // identify-policy-step - the policy sourceType objects to fill the select/dropdown
  sourceTypes: [
    { id: 'edrPolicy', policyType: 'edrPolicy', name: 'EndpointScan', label: 'adminUsm.policyWizard.edrSourceType' }
    // { id: 'fileLogPolicy', policyType: 'fileLogPolicy', name: 'EndpointFile', label: 'adminUsm.policyWizard.fileLogSourceType' },
    // { id: 'windowsLogPolicy', policyType: 'windowsLogPolicy', name: 'EndpointWL', label: 'adminUsm.policyWizard.windowsLogSourceType' }
  ],

  // define-policy-step - available settings to render the left col
  availableSettings: [
    { index: 0, id: 'scanScheduleLabel', label: 'adminUsm.policy.scanSchedule', isHeader: true, isEnabled: true },
    { index: 1, id: 'schedOrManScan', label: 'adminUsm.policy.schedOrManScan', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/scan-schedule' },
    { index: 2, id: 'effectiveDate', label: 'adminUsm.policy.effectiveDate', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/effective-date' },
    { index: 3, id: 'recIntervalSubHeader', label: 'adminUsm.policy.recurrenceInterval', isSubHeader: true, isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan' },
    { index: 4, id: 'recurrenceInterval', label: 'adminUsm.policy.scanFrequency', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/recurrence-interval' },
    { index: 5, id: 'startTime', label: 'adminUsm.policy.startTime', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/start-time' },
    { index: 6, id: 'maxUsageSubHeader', label: 'adminUsm.policy.maximumProcessorUsage', isSubHeader: true, isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan' },
    { index: 7, id: 'cpuMax', label: 'adminUsm.policy.cpuMaximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/cpu-max' },
    { index: 8, id: 'vmMax', label: 'adminUsm.policy.vmMaximum', isEnabled: true, isGreyedOut: true, parentId: 'schedOrManScan', callback: 'usm-policies/policy/schedule-config/vm-max' },
    { index: 9, id: 'advScanSettingsHeader', label: 'adminUsm.policy.advScanSettings', isHeader: true, isEnabled: true },
    { index: 10, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true }
  ],
  // define-policy-step - selected settings to render the right col
  selectedSettings: [],

  policyStatus: null, // wait, complete, error

  // keeps track of the form fields visited by the user
  visited: []
};

const scanScheduleId = 'schedOrManScan';
const scanSchedLabelId = 'scanScheduleLabel';

// Private method used to determine if a top level label like "SCAN SCHEDULE" or "ADVANCED SCAN SETTINGS"
// needs to be shown in the Selected settings vbox on the right.
// Suppose a child component from "SCAN SCHEDULE" is moved to the right, we need to show the "SCAN SCHEDULE"
// label on the right too for that child component.
const _shouldShowLabelInSelSettings = (selectedSettingsIds, matchingIds) => {
  let showLabel = false;
  const { length } = selectedSettingsIds;
  for (let i = 0; i < length; ++i) {
    if (_.indexOf(selectedSettingsIds, matchingIds[i]) !== -1) {
      showLabel = true;
      break;
    }
  }
  return showLabel;
};

// If determined that a particular label needs to be shown on the right, this method will add that label
// to the selectedSettings array.
const _addLabelToSelSettings = (state, labelId) => {
  const { selectedSettings, availableSettings } = state;
  const labelToAdd = availableSettings.find((d) => d.id === labelId);
  return _.uniqBy([ ...selectedSettings, labelToAdd ], 'id');
};

export default reduxActions.handleActions({

  [ACTION_TYPES.NEW_POLICY]: (state /* , action */) => {
    // reset everything
    const newState = state.merge({
      ...initialState,
      policyStatus: 'complete'
    });
    // set default scanStartDate to today
    const fields = 'policy.scheduleConfig.scheduleOptions.scanStartDate'.split('.');
    const scanStartDateToday = moment().format('YYYY-MM-DD');
    return newState.setIn(fields, scanStartDateToday);
  },

  [ACTION_TYPES.GET_POLICY]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          ...initialState,
          policyStatus: 'wait'
        });
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
  ),

  // define-policy-step - add an available setting (left col) as a selected setting (right col)
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

  // define-policy-step - when stuff gets moved from left col to right col, add appropriate labels to the right col
  [ACTION_TYPES.ADD_LABEL_TO_SELECTED_SETTINGS]: (state) => {
    const { selectedSettings } = state;
    let newSelectedSettings = [...selectedSettings];

    const selectedSettingsIds = _.map(selectedSettings, 'id'); // ["schedOrManScan", "scanScheduleLabel", ...]
    const showScanSchedLabelInSelSettings = _shouldShowLabelInSelSettings(selectedSettingsIds, [scanScheduleId]);

    if (showScanSchedLabelInSelSettings) {
      newSelectedSettings = _addLabelToSelSettings(state, scanSchedLabelId);
    }

    return state.merge({
      selectedSettings: newSelectedSettings
    });
  },

  // define-policy-step - remove a selected setting (right col) and add back as an available setting (left col)
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

  // define-policy-step -
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
        policy: {
          scheduleConfig: { ...initialState.scheduleConfig }
        },
        availableSettings: newAvailableSettings
      }, { deep: true }); // deep merge so we don't reset everything
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
        policy: {
          scheduleConfig: { ...initialState.scheduleConfig }
        },
        availableSettings: newAvailableSettings,
        selectedSettings: selectedSettings.filter((el) => el.parentId !== scanScheduleId)
      }, { deep: true }); // deep merge so we don't reset everything
      const scheduleOptions = 'policy.scheduleConfig.scheduleOptions'.split('.');
      const scanOptions = 'policy.scheduleConfig.scanOptions'.split('.');
      return newState.setIn(scanType, payload).setIn(scheduleOptions, null).setIn(scanOptions, null);
    }
  },

  // define-policy-step -
  [ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS]: (state) => {
    // when scan schedule is removed from selected settings, all it's child components
    // (effective date, recurrence interval, processor usage) should be removed.
    // so reset the scheduleConfig & available/selected settings to defaults
    return state.merge({
      policy: {
        scheduleConfig: { ...initialState.scheduleConfig }
      },
      availableSettings: [ ...initialState.availableSettings ],
      selectedSettings: [ ...initialState.selectedSettings ]
    }, { deep: true }); // deep merge so we don't reset everything
  },

  [ACTION_TYPES.EDIT_POLICY]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    // Edit the value in the policy, and keep track of the field as having been visited
    // Visited fields will show error/validation messages
    return state.setIn(fields, value).set('visited', _.uniq([...state.visited, field]));
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
  ),

  [ACTION_TYPES.SAVE_PUBLISH_POLICY]: (state, action) => (
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
