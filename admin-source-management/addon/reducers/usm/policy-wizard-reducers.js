import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { isBlank } from '@ember/utils';
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
    primaryAddress: null,
    primaryNwServiceId: null,
    primaryHttpsPort: null, // 1 to 65535
    primaryHttpsBeaconInterval: null, // 15 (900 secs is 15 mins)
    primaryHttpsBeaconIntervalUnit: null, // 'MINUTES' | 'HOURS'
    primaryUdpPort: null, // 1 to 65535
    primaryUdpBeaconInterval: null, // 30 (seconds)
    primaryUdpBeaconIntervalUnit: null, // 'SECONDS' | 'MINUTES'
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
      prevStepId: '',
      nextStepId: 'definePolicyStep',
      title: 'adminUsm.policyWizard.identifyPolicy',
      stepComponent: 'usm-policies/policy-wizard/identify-policy-step',
      titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    },
    {
      id: 'definePolicyStep',
      prevStepId: 'identifyPolicyStep',
      nextStepId: '',
      title: 'adminUsm.policyWizard.definePolicy',
      stepComponent: 'usm-policies/policy-wizard/define-policy-step',
      titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
      toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar'
    }
  ],

  // identify-policy-step - the policy sourceType objects to fill the select/dropdown
  sourceTypes: [
    { id: 'edrPolicy', policyType: 'edrPolicy', name: 'EndpointScan', label: 'adminUsm.policyWizard.edrSourceType' }
    // { id: 'fileLogPolicy', policyType: 'fileLogPolicy', name: 'EndpointFile', label: 'adminUsm.policyWizard.fileLogSourceType' },
    // { id: 'windowsLogPolicy', policyType: 'windowsLogPolicy', name: 'EndpointWL', label: 'adminUsm.policyWizard.windowsLogSourceType' }
  ],

  // list of endpoint servers from the orchestration service to populate the hostname
  // drop down
  listOfEndpointServers: [],

  // define-policy-step - available settings to render the left col
  // * make sure the id is always the same as the policy property name
  availableSettings: [
    { index: 0, id: 'scanScheduleHeader', label: 'adminUsm.policy.scanSchedule', isHeader: true, isEnabled: true, isGreyedOut: true },
    { index: 1, id: 'scanType', label: 'adminUsm.policy.schedOrManScan', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'scanType', value: 'MANUAL' }] },
    { index: 2, id: 'scanStartDate', label: 'adminUsm.policy.effectiveDate', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/effective-date', defaults: [{ field: 'scanStartDate', value: moment().format('YYYY-MM-DD') }] },
    { index: 3, id: 'recurrenceInterval', label: 'adminUsm.policy.scanFrequency', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/recurrence-interval', defaults: [{ field: 'recurrenceInterval', value: 1 }, { field: 'recurrenceUnit', value: 'DAYS' }] },
    { index: 4, id: 'scanStartTime', label: 'adminUsm.policy.startTime', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/start-time', defaults: [{ field: 'scanStartTime', value: '09:00' }] },
    { index: 5, id: 'cpuMax', label: 'adminUsm.policy.cpuMax', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/cpu-max', defaults: [{ field: 'cpuMax', value: 90 }] },
    { index: 6, id: 'cpuMaxVm', label: 'adminUsm.policy.vmMaximum', isEnabled: true, isGreyedOut: true, parentId: 'scanType', component: 'usm-policies/policy/schedule-config/vm-max', defaults: [{ field: 'cpuMaxVm', value: 90 }] },
    { index: 7, id: 'advScanSettingsHeader', label: 'adminUsm.policy.advScanSettings', isHeader: true, isEnabled: true },
    { index: 8, id: 'captureFloatingCode', label: 'adminUsm.policy.captureFloatingCode', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'captureFloatingCode', value: true }] },
    { index: 9, id: 'downloadMbr', label: 'adminUsm.policy.downloadMbr', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'downloadMbr', value: false }] },
    { index: 10, id: 'filterSignedHooks', label: 'adminUsm.policy.filterSignedHooks', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'filterSignedHooks', value: false }] },
    { index: 11, id: 'requestScanOnRegistration', label: 'adminUsm.policy.requestScanOnRegistration', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'requestScanOnRegistration', value: false }] },
    { index: 12, id: 'invActionsHeader', label: 'adminUsm.policy.invasiveActions', isHeader: true, isEnabled: true },
    { index: 13, id: 'blockingEnabled', label: 'adminUsm.policy.blockingEnabled', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'blockingEnabled', value: false }] },
    { index: 14, id: 'endpointServerHeader', label: 'adminUsm.policy.endpointServerSettings', isHeader: true, isEnabled: true },
    { index: 15, id: 'primaryAddress', label: 'adminUsm.policy.primaryAddress', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/primary-address', defaults: [{ field: 'primaryAddress', value: '' }, { field: 'primaryNwServiceId', value: '' }] },
    { index: 16, id: 'primaryHttpsPort', label: 'adminUsm.policy.primaryHttpsPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryHttpsPort', value: 443 }] },
    { index: 17, id: 'primaryHttpsBeaconInterval', label: 'adminUsm.policy.primaryHttpsBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryHttpsBeaconInterval', value: 15 }, { field: 'primaryHttpsBeaconIntervalUnit', value: 'MINUTES' }] },
    { index: 18, id: 'primaryUdpPort', label: 'adminUsm.policy.primaryUdpPort', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-ports', defaults: [{ field: 'primaryUdpPort', value: 444 }] },
    { index: 19, id: 'primaryUdpBeaconInterval', label: 'adminUsm.policy.primaryUdpBeaconInterval', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-beacons', defaults: [{ field: 'primaryUdpBeaconInterval', value: 30 }, { field: 'primaryUdpBeaconIntervalUnit', value: 'SECONDS' }] },
    { index: 20, id: 'agentSettingsHeader', label: 'adminUsm.policy.agentSettings', isHeader: true, isEnabled: true },
    { index: 21, id: 'agentMode', label: 'adminUsm.policy.agentMode', isEnabled: true, isGreyedOut: false, parentId: null, component: 'usm-policies/policy/schedule-config/usm-radios', defaults: [{ field: 'agentMode', value: 'NO_MONITORING' }] }
  ],
  // define-policy-step - selected settings to render the right col
  selectedSettings: [],

  // keeps track of the form fields visited by the user
  visited: []
};

const scanScheduleId = 'scanType';
const allScanScheduleIds = ['scanType', 'scanStartDate', 'recurrenceInterval', 'scanStartTime', 'cpuMax', 'cpuMaxVm'];
const scanSchedHeaderId = 'scanScheduleHeader';
const allAdvScanSettingsIds = ['captureFloatingCode', 'downloadMbr', 'filterSignedHooks', 'requestScanOnRegistration'];
const advScanSettingsHeaderId = 'advScanSettingsHeader';
const invActionsHeaderId = 'invActionsHeader';
const allInvActionsIds = 'blockingEnabled';
const endpointServerHeaderId = 'endpointServerHeader';
const allEndpointServerIds = ['primaryAddress', 'primaryHttpsPort', 'primaryHttpsBeaconInterval', 'primaryUdpPort', 'primaryUdpBeaconInterval'];
const agentSettingsHeaderId = 'agentSettingsHeader';
const allAgentSettingsIds = 'agentMode';

// Private method used to determine if a top level header like "SCAN SCHEDULE" or "ADVANCED SCAN SETTINGS"
// needs to be shown in the Selected settings vbox on the right.
// Suppose a child component from "SCAN SCHEDULE" is moved to the right, we need to show the "SCAN SCHEDULE"
// header on the right too for that child component.
const _shouldShowHeaderInSelSettings = (selectedSettingsIds, matchingIds) => {
  let showHeader = false;
  const { length } = matchingIds;
  for (let i = 0; i < length; ++i) {
    if (_.indexOf(selectedSettingsIds, matchingIds[i]) !== -1) {
      showHeader = true;
      break;
    }
  }
  return showHeader;
};

// Private method that does a find of headerId in the availableSettings array,
// and returns that entire object.
const _findHeaderInAvailSettings = (availableSettings, headerId) => {
  return availableSettings.find((d) => d.id === headerId);
};

// Private method that returns a new selectedSettings array after removing the headerId
const _removeHeaderFromSelSettings = (selectedSettings, headerId) => {
  return selectedSettings.filter((el) => el.id !== headerId);
};

// Private method used to determine if a top level header like "SCAN SCHEDULE" or "ADVANCED SCAN SETTINGS"
// needs to be shown in the Available settings vbox on the left.
// If all the componenents under a header is moved to the right, this method changes the header's isEnabled flag
// to false, meaning the header will not appear on left anymore
// if even one component under the header is still on the left, the header stays intact.
const _shouldShowHeaderInAvailSettings = (subset, superset, el) => {
  // check if the superset contains all elements from the subset
  if (_.difference(subset, superset).length === 0) {
    return {
      ...el,
      isEnabled: false
    };
  } else {
    return {
      ...el,
      isEnabled: true
    };
  }
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
          // settings already set in the fetched policy are added to selectedSettings
          if (!isBlank(fetchedPolicy[setting.id])) {
            newAvailableSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
            newSelectedSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
          // settings dependent on scanType of 'SCHEDULED' must be enabled
          } else if (setting.parentId === scanScheduleId && fetchedPolicy[setting.parentId] === 'SCHEDULED') {
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

  // define-policy-step - when stuff gets moved from left col to right col or viceversa, this handles updating
  // the headers correctly.
  [ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS]: (state) => {
    const { availableSettings, selectedSettings } = state;
    let newSelectedSettings = [...selectedSettings];

    const selectedSettingsIds = _.map(selectedSettings, 'id'); // ["scanType", "scanScheduleHeader", ...]

    const showScanSchedHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, allScanScheduleIds);
    if (showScanSchedHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, scanSchedHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, scanSchedHeaderId);
    }

    const showadvScanSettingsHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, allAdvScanSettingsIds);
    if (showadvScanSettingsHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, advScanSettingsHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, advScanSettingsHeaderId);
    }

    const showInvActionsHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, [allInvActionsIds]);
    if (showInvActionsHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, invActionsHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, invActionsHeaderId);
    }

    const showEndpointServerHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, allEndpointServerIds);
    if (showEndpointServerHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, endpointServerHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, endpointServerHeaderId);
    }

    const showAgentSettingsHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, [allAgentSettingsIds]);
    if (showAgentSettingsHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, agentSettingsHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, agentSettingsHeaderId);
    }

    const newAvailableSettings = availableSettings.map((el) => {
      if (el.id === scanSchedHeaderId) {
        return _shouldShowHeaderInAvailSettings(allScanScheduleIds, selectedSettingsIds, el);
      }
      if (el.id === advScanSettingsHeaderId) {
        return _shouldShowHeaderInAvailSettings(allAdvScanSettingsIds, selectedSettingsIds, el);
      }
      if (el.id === invActionsHeaderId) {
        return _shouldShowHeaderInAvailSettings([allInvActionsIds], selectedSettingsIds, el);
      }
      if (el.id === endpointServerHeaderId) {
        return _shouldShowHeaderInAvailSettings(allEndpointServerIds, selectedSettingsIds, el);
      }
      if (el.id === agentSettingsHeaderId) {
        return _shouldShowHeaderInAvailSettings([allAgentSettingsIds], selectedSettingsIds, el);
      }
      return el;
    });

    return state.merge({
      availableSettings: newAvailableSettings,
      selectedSettings: _.uniqBy(newSelectedSettings, 'id')
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

  // define-policy-step - When RESET_SCAN_SCHEDULE is dispatched, everything under Scan Schedule
  // should be set to default state and moved to the left. Other selected settings will remain as it is.
  [ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS]: (state) => {
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
        scanType: initialState.policy.scanType,
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
  ),

  [ACTION_TYPES.FETCH_ENDPOINT_SERVERS]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('listOfEndpointServers', []);
      },
      success: (state) => {
        return state.set('listOfEndpointServers', action.payload.data);
      }
    })
  )

}, Immutable.from(initialState));
