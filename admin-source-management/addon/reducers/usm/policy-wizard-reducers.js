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
    policyType: 'edrPolicy',
    name: null,
    description: null,
    dirty: true,
    lastPublishedCopy: null,
    lastPublishedOn: 0,
    // scheduleConfig
    scanType: null, // 'MANUAL' | 'SCHEDULED'
    // scheduleOptions
    scanStartDate: null, // YYYY-MM-DD
    scanStartTime: null, // '10:00'
    recurrenceInterval: null, // 1
    recurrenceUnit: null, // 'DAYS' | 'WEEKS'
    runOnDaysOfWeek: null, // array containing day name (names eventually) ex. ['MONDAY']
    // scanOptions
    cpuMax: null, // 75
    cpuMaxVm: null, // 85
    captureFloatingCode: null, // true or false
    downloadMbr: null, // true or false
    filterSignedHooks: null, // true or false
    requestScanOnRegistration: null, // true or false
    blockingEnabled: null, // true or false
    agentMode: null // 'NO_MONITORING' | 'FULL_MONITORING'
  },
  policyStatus: null, // wait, complete, error

  // the summary list of policies objects to fill the group select/dropdown
  policyList: [],
  policyListStatus: null, // wait, complete, error

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
  // * make sure the id is always the same as the policy property name
  availableSettings: [
    { index: 0, id: 'scanScheduleLabel', label: 'adminUsm.policy.scanSchedule', isHeader: true, isEnabled: true },
    { index: 1, id: 'scanType', label: 'adminUsm.policy.schedOrManScan', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
    { index: 2, id: 'scanStartDate', label: 'adminUsm.policy.effectiveDate', isEnabled: true, isGreyedOut: true, parentId: 'scanType', callback: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] },
    { index: 3, id: 'recIntervalSubHeader', label: 'adminUsm.policy.recurrenceInterval', isSubHeader: true, isEnabled: true, isGreyedOut: true, parentId: 'scanType' },
    { index: 4, id: 'recurrenceInterval', label: 'adminUsm.policy.scanFrequency', isEnabled: true, isGreyedOut: true, parentId: 'scanType', callback: 'usm-policies/policy/schedule-config/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'DAYS' }] },
    { index: 5, id: 'scanStartTime', label: 'adminUsm.policy.startTime', isEnabled: true, isGreyedOut: true, parentId: 'scanType', callback: 'usm-policies/policy/schedule-config/start-time', defaults: [{ field: 'scanStartTime', value: '10:00' }] },
    { index: 6, id: 'maxUsageSubHeader', label: 'adminUsm.policy.maximumProcessorUsage', isSubHeader: true, isEnabled: true, isGreyedOut: true, parentId: 'scanType' },
    { index: 7, id: 'cpuMax', label: 'adminUsm.policy.cpuMax', isEnabled: true, isGreyedOut: true, parentId: 'scanType', callback: 'usm-policies/policy/schedule-config/cpu-max', defaults: [{ field: 'cpuMax', value: 75 }] },
    { index: 8, id: 'cpuMaxVm', label: 'adminUsm.policy.vmMaximum', isEnabled: true, isGreyedOut: true, parentId: 'scanType', callback: 'usm-policies/policy/schedule-config/vm-max', defaults: [{ field: 'cpuMaxVm', value: 85 }] },
    { index: 9, id: 'advScanSettingsLabel', label: 'adminUsm.policy.advScanSettings', isHeader: true, isEnabled: true },
    { index: 10, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'captureFloatingCode', value: false }] },
    { index: 11, id: 'downloadMbr', label: 'adminUsm.policy.downloadMbr', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'downloadMbr', value: false }] },
    { index: 12, id: 'includeHooksSubHeader', label: 'adminUsm.policy.includeHooks', isSubHeader: true, isEnabled: true, isGreyedOut: false, parentId: null },
    { index: 13, id: 'filterSignedHooks', label: 'adminUsm.policy.filterSignedHooks', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
    { index: 14, id: 'autoLaunchScanSubHeader', label: 'adminUsm.policy.autoLaunchScan', isSubHeader: true, isEnabled: true, isGreyedOut: false, parentId: null },
    { index: 15, id: 'requestScanOnRegistration', label: 'adminUsm.policy.requestScanOnRegistration', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
    { index: 16, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
    { index: 17, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
    { index: 18, id: 'agentSettingsHeader', label: 'adminUsm.policy.agentSettings', isHeader: true, isEnabled: true },
    { index: 19, id: 'agentMode', label: 'adminUsm.policy.agentMode', isEnabled: true, isGreyedOut: false, parentId: null, callback: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'agentMode', value: 'NO_MONITORING' }] }
  ],
  // define-policy-step - selected settings to render the right col
  selectedSettings: [],

  // keeps track of the form fields visited by the user
  visited: []
};

const scanScheduleId = 'scanType';
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
    return state.merge({
      ...initialState,
      policyStatus: 'complete'
    });
  },

  [ACTION_TYPES.FETCH_POLICY]: (state, action) => (
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
        const fetchedPolicy = action.payload.data;
        const newAvailableSettings = [];
        const newSelectedSettings = [];
        for (let i = 0; i < state.availableSettings.length; i++) {
          const setting = state.availableSettings[i];
          // settings already set in the fetched policy
          if (fetchedPolicy[setting.id]) {
            newAvailableSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
            newSelectedSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
          // settings with parent settings already set in the fetched policy
          } else if (fetchedPolicy[setting.parentId]) {
            newAvailableSettings.push({ ...setting, isEnabled: true, isGreyedOut: false });
          // default
          } else {
            newAvailableSettings.push({ ...setting });
          }
        }
        return state.merge({
          policy: fetchedPolicy,
          availableSettings: newAvailableSettings,
          selectedSettings: newSelectedSettings,
          policyStatus: 'complete'
        });
      }
    })
  ),

  [ACTION_TYPES.FETCH_POLICY_LIST]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.merge({
          policyList: [],
          policyListStatus: 'wait'
        });
      },
      failure: (state) => {
        return state.set('policyListStatus', 'error');
      },
      success: (state) => {
        return state.merge({
          policyList: action.payload.data,
          policyListStatus: 'complete'
        });
      }
    })
  ),

  // define-policy-step - add an available setting (left col) as a selected setting (right col)
  [ACTION_TYPES.ADD_TO_SELECTED_SETTINGS]: (state, { payload }) => {
    const id = payload;
    const { selectedSettings, availableSettings } = state;

    const policyValues = {};
    const newSelectedSettings = availableSettings.find((d) => d.id === id);
    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === id) {
        // add the added setting's defaults to the policy
        const elDefaults = el.defaults || [];
        for (let i = 0; i < elDefaults.length; i++) {
          policyValues[elDefaults[i].field] = elDefaults[i].value;
        }
        return {
          ...el,
          isEnabled: false
        };
      }
      // if the scan type is "SCHEDULED" in state, nothing should be greyed out
      // in availableSettings
      if (state.policy.scanType === 'SCHEDULED') {
        return {
          ...el,
          isGreyedOut: false
        };
      }
      return el;
    });
    return state.merge({
      policy: {
        ...policyValues
      },
      availableSettings: newAvailableSettings,
      selectedSettings: _.uniqBy([ ...selectedSettings, newSelectedSettings ], 'id')
    }, { deep: true }); // deep merge so we don't reset everything
  },

  // define-policy-step - when stuff gets moved from left col to right col, add appropriate labels to the right col
  [ACTION_TYPES.ADD_LABEL_TO_SELECTED_SETTINGS]: (state) => {
    const { selectedSettings } = state;
    let newSelectedSettings = [...selectedSettings];

    const selectedSettingsIds = _.map(selectedSettings, 'id'); // ["scanType", "scanScheduleLabel", ...]
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

    const policyValues = {};
    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === id) {
        // remove the removed setting's values from the policy
        const elDefaults = el.defaults || [];
        for (let i = 0; i < elDefaults.length; i++) {
          policyValues[elDefaults[i].field] = null;
        }
        return {
          ...el,
          isEnabled: true
        };
      }
      return el;
    });
    return state.merge({
      policy: {
        ...policyValues
      },
      availableSettings: newAvailableSettings,
      selectedSettings: selectedSettings.filter((el) => el.id !== id)
    }, { deep: true }); // deep merge so we don't reset everything
  },

  // define-policy-step -
  [ACTION_TYPES.TOGGLE_SCAN_TYPE]: (state, { payload }) => {
    const { availableSettings, selectedSettings } = state;
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
      return state.merge({
        policy: {
          scanType: payload
        },
        availableSettings: newAvailableSettings
      }, { deep: true }); // deep merge so we don't reset everything
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
  },

  // define-policy-step -
  [ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS]: (state) => {
    // when scan schedule is removed from selected settings, all it's child components
    // (effective date, recurrence interval, processor usage) should be removed.
    // so reset the scheduleConfig & available/selected settings to defaults
    return state.merge({
      policy: {
        scanType: initialState.policy.scanType,
        scanStartDate: null,
        scanStartTime: null,
        recurrenceInterval: null,
        recurrenceUnit: null,
        runOnDaysOfWeek: null,
        cpuMax: null,
        cpuMaxVm: null
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

  [ACTION_TYPES.UPDATE_POLICY_PROPERTY]: (state, action) => {
    let newState = state;
    const fieldValuePairs = action.payload;
    for (let i = 0; i < fieldValuePairs.length; i++) {
      const { field, value } = fieldValuePairs[i];
      const fields = field.split('.');
      // Edit the value in the policy, and keep track of the field as having been visited
      // Visited fields will show error/validation messages
      newState = newState.setIn(fields, value).set('visited', _.uniq([...state.visited, field]));
    }
    return newState;
  },

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
