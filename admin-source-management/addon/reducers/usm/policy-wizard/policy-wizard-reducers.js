import Immutable from 'seamless-immutable';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { isBlank } from '@ember/utils';
import _ from 'lodash';
import edrPolicyInitialState from './edrPolicy/edr-initialState';
import edrPolicyReducers from './edrPolicy/edr-reducerFns';
import windowsLogPolicyReducers from './windowsLogPolicy/windowsLog-reducerFns';
import windowsLogPolicyInitialState from './windowsLogPolicy/windowsLog-initialState';
import * as ACTION_TYPES from 'admin-source-management/actions/types';

const INITIAL_STATES = {
  edrPolicy: edrPolicyInitialState,
  windowsLogPolicy: windowsLogPolicyInitialState,
  common: {
    //  the policy object to be created/updated/saved
    policy: {
      // common policy props
      id: null,
      policyType: 'edrPolicy', // need a default for initialization
      name: '',
      description: '',
      dirty: true,
      lastPublishedCopy: null,
      lastPublishedOn: 0,
      defaultPolicy: false,
      createdOn: 0
      // policy type specific props will be merged in each time we run:
      // - NEW_POLICY, FETCH_POLICY (edit), and UPDATE_POLICY_TYPE
    },
    policyOrig: {},
    policyStatus: null, // wait, complete, error

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
        toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar',
        prevButtonDisabled: true,
        nextButtonDisabled: false,
        saveButtonDisabled: true,
        publishButtonDisabled: true,
        showErrors: false
      },
      {
        id: 'definePolicyStep',
        prevStepId: 'identifyPolicyStep',
        nextStepId: '',
        title: 'adminUsm.policyWizard.definePolicy',
        stepComponent: 'usm-policies/policy-wizard/define-policy-step',
        titlebarComponent: 'usm-policies/policy-wizard/policy-titlebar',
        toolbarComponent: 'usm-policies/policy-wizard/policy-toolbar',
        prevButtonDisabled: false,
        nextButtonDisabled: true,
        saveButtonDisabled: false,
        publishButtonDisabled: false,
        showErrors: false
      }
    ],

    // identify-policy-step - the policy sourceType objects to fill the select/dropdown
    sourceTypes: [
      { id: 'edrPolicy', policyType: 'edrPolicy', name: 'EndpointScan', label: 'adminUsm.policyWizard.edrSourceType' },
      // { id: 'fileLogPolicy', policyType: 'fileLogPolicy', name: 'EndpointFile', label: 'adminUsm.policyWizard.fileLogSourceType' },
      { id: 'windowsLogPolicy', policyType: 'windowsLogPolicy', name: 'EndpointWL', label: 'adminUsm.policyWizard.windowsLogSourceType' }
    ],

    // define-policy-step - available settings to render the left col
    // policy type specific available settings will be merged in each time we run:
    // - NEW_POLICY, FETCH_POLICY (edit), and UPDATE_POLICY_TYPE
    availableSettings: [],
    // define-policy-step - selected settings to render the right col
    selectedSettings: [],

    // keeps track of the form fields visited by the user
    visited: [],

    // the summary list of policies objects
    policyList: [],
    policyListStatus: null, // wait, complete, error

    // ===================================================
    // edrPolicy specific state to be fetched
    // ===================================================
    // list of endpoint servers from the orchestration service to populate the hostname drop down
    listOfEndpointServers: [],

    // ===================================================
    // windowsLogPolicy specific state to be fetched
    // ===================================================
    listOfLogServers: []
  }
};

const scanScheduleId = 'scanType';
const allScanScheduleIds = ['scanType', 'scanStartDate', 'recurrenceInterval', 'scanStartTime', 'cpuMax', 'cpuMaxVm'];
const scanSchedHeaderId = 'scanScheduleHeader';
const allAdvScanSettingsIds = ['scanMbr', 'requestScanOnRegistration'];
const advScanSettingsHeaderId = 'advScanSettingsHeader';
const invActionsHeaderId = 'invActionsHeader';
const allInvActionsIds = 'blockingEnabled';
const endpointServerHeaderId = 'endpointServerHeader';
const allEndpointServerIds = ['primaryAddress', 'primaryHttpsPort', 'primaryHttpsBeaconInterval', 'primaryUdpPort', 'primaryUdpBeaconInterval'];
const agentSettingsHeaderId = 'agentSettingsHeader';
const allAgentSettingsIds = 'agentMode';
const advancedConfigHeaderId = 'advancedConfigHeader';
const allAdvancedConfigIds = 'customConfig';

// run for NEW_POLICY, FETCH_POLICY (edit), and UPDATE_POLICY_TYPE
export const buildInitialState = (state, policyType, isUpdatePolicyType = false) => {
  // reset everything from common initialState & type specific initialState
  let mergedInitialState = state.set('policy', {}).merge(
    [{ ...INITIAL_STATES.common }, { ...INITIAL_STATES[policyType] }],
    { deep: true }
  );
  // keep things we don't want to refetch or overwrite for UPDATE_POLICY_TYPE
  if (isUpdatePolicyType) {
    mergedInitialState = mergedInitialState.merge({
      policyList: [...state.policyList]
    });
  }
  return mergedInitialState;
};

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
    // reset everything from common initialState & type specific initialState
    const mergedInitialState = buildInitialState(state, state.policy.policyType);
    return mergedInitialState.set('policyStatus', 'complete');
  },

  [ACTION_TYPES.FETCH_POLICY]: (state, action) => (
    handle(state, action, {
      start: (state) => {
        return state.set('policyStatus', 'wait');
      },
      failure: (state) => {
        return state.set('policyStatus', 'error');
      },
      success: (state) => {
        const fetchedPolicy = action.payload.data;
        // reset everything from common initialState & type specific initialState
        const mergedInitialState = buildInitialState(state, fetchedPolicy.policyType);
        const newAvailableSettings = [];
        const newSelectedSettings = [];
        for (let i = 0; i < mergedInitialState.availableSettings.length; i++) {
          const setting = mergedInitialState.availableSettings[i];
          // settings already set in the fetched policy are added to selectedSettings
          if (!isBlank(fetchedPolicy[setting.id])) {
            newAvailableSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
            newSelectedSettings.push({ ...setting, isEnabled: false, isGreyedOut: false });
          // settings dependent on scanType of 'ENABLED' must be enabled
          } else if (setting.parentId === scanScheduleId && fetchedPolicy[setting.parentId] === 'ENABLED') {
            newAvailableSettings.push({ ...setting, isEnabled: true, isGreyedOut: false });
          // default
          } else {
            newAvailableSettings.push({ ...setting });
          }
        }
        return mergedInitialState.merge({
          policy: fetchedPolicy,
          policyOrig: fetchedPolicy,
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

  [ACTION_TYPES.UPDATE_POLICY_STEP]: (state, action) => {
    const { field, value } = action.payload;
    const fields = field.split('.');
    return state.setIn(fields, value);
  },

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
      // if the scan type is "ENABLED" in state, nothing should be greyed out
      // in availableSettings
      if (state.policy.scanType === 'ENABLED') {
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

    const showAdvancedConfigHeader = _shouldShowHeaderInSelSettings(selectedSettingsIds, [allAdvancedConfigIds]);
    if (showAdvancedConfigHeader) {
      newSelectedSettings.push(_findHeaderInAvailSettings(availableSettings, advancedConfigHeaderId));
    } else {
      newSelectedSettings = _removeHeaderFromSelSettings(newSelectedSettings, advancedConfigHeaderId);
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
      if (el.id === advancedConfigHeaderId) {
        return _shouldShowHeaderInAvailSettings([allAdvancedConfigIds], selectedSettingsIds, el);
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

  // edrPolicy actions
  [ACTION_TYPES.FETCH_ENDPOINT_SERVERS]: edrPolicyReducers.fetchEndpointServers,

  [ACTION_TYPES.EDR_DEFAULT_POLICY]: edrPolicyReducers.edrDefaultPolicy,

  // windowsLogPolicy actions
  [ACTION_TYPES.FETCH_LOG_SERVERS]: windowsLogPolicyReducers.fetchLogServers,

  // define-policy-step -
  [ACTION_TYPES.TOGGLE_SCAN_TYPE]: edrPolicyReducers.toggleScanType,

  // define-policy-step - When RESET_SCAN_SCHEDULE is dispatched, everything under Scan Schedule
  // should be set to default state and moved to the left. Other selected settings will remain as it is.
  [ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS]: edrPolicyReducers.resetScanScheduleToDefaults,

  // identify-policy-step
  [ACTION_TYPES.UPDATE_POLICY_TYPE]: (state, action) => {
    const policyType = action.payload;
    // reset everything from common initialState & type specific initialState
    const mergedInitialState = buildInitialState(state, policyType, true);
    // update the policy type
    return mergedInitialState.setIn('policy.policyType'.split('.'), policyType);
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
          policyOrig: action.payload.data,
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
          policyOrig: action.payload.data,
          policyStatus: 'complete'
        });
      }
    })
  )

}, Immutable.from(INITIAL_STATES.common));
