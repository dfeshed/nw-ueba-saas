import RSVP from 'rsvp';
import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const scanScheduleId = 'scanType';
const noop = () => {};
const callbacksDefault = { onSuccess() {}, onFailure() {} };

/**
 * Initialize the policy wizard for create-new policy & editing an existing policy.
 * - create-new initializes everything assuming a blank policy with a default policyType (currently 'edrPolicy')
 * - passing in an existing policy ID initializes everything by loading the policy by the ID,
 *   and then initializing actions specific to the fetched policy by policyType
 * @param policyId
 * @public
 */
const initializePolicy = (policyId) => {
  return async(dispatch, getState) => {
    if (policyId === 'create-new') {
      dispatch(newPolicy());
    } else {
      try {
        await _initializeFetchPolicy(policyId, dispatch, getState);
      } catch (e) {
        // since the fetch failed - no need to dispatch or load anything else
        return;
      }
    }

    // init policy list, which is used to validate policy name uniqueness
    dispatch(fetchPolicyList());
    // init type specific actions
    // isDefaultPolicy is a boolean - for a default policy this will be set to true.
    const { defaultPolicy: isDefaultPolicy } = getState().usm.policyWizard.policy;
    initializePolicyType(getState().usm.policyWizard.policy.policyType, dispatch, isDefaultPolicy);
  };
};

/**
 * Wraps the fetching of a policy in a promise
 * @see fetchPolicy
 * @private
 */
const _initializeFetchPolicy = (policyId, dispatch, getState) => {
  return new RSVP.Promise((resolve, reject) => {
    fetchPolicy(policyId, callbacksDefault, resolve, reject)(dispatch, getState);
  });
};

/**
 * Dispatches actions needed to initialize a specific policy type for:
 * - NEW_POLICY, FETCH_POLICY (edit), and UPDATE_POLICY_TYPE
 * @private
 */
const initializePolicyType = (policyType, dispatch, isDefaultPolicy = false) => {
  // init type specific actions
  switch (policyType) {
    // edrPolicy picked from the dropdown
    case 'edrPolicy':
      dispatch(fetchEndpointServers());
      // if the policy is a defaultPolicy, also dispatch it's specific action creator.
      if (isDefaultPolicy) {
        dispatch(edrDefaultPolicy());
      }
      break;
    // windowsLogPolicy picked from the dropdown
    case 'windowsLogPolicy':
      dispatch(fetchLogServers());
      break;
    // filePolicy picked from the dropdown
    case 'filePolicy':
      dispatch(fetchLogServers());
      dispatch(fetchFileSourceTypes());
      break;
  }
};

/**
 * Replaces any previous policy state with the template for a brand new policy
 * @public
 */
const newPolicy = () => ({ type: ACTION_TYPES.NEW_POLICY });

/**
 * Fetches a single policy for edit. It also dispatches _updateHeadersForAllSettings
 * so that correct headers will be intact for the policy that is fetched.
 * @public
 */
const fetchPolicy = (id, callbacks = callbacksDefault, resolve = noop, reject = noop) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_POLICY,
      promise: policyAPI.fetchPolicy(id),
      meta: {
        onSuccess: (response) => {
          callbacks.onSuccess(response);
          dispatch(_updateHeadersForAllSettings());
          resolve();
        },
        onFailure: (response) => {
          callbacks.onFailure(response);
          reject();
        }
      }
    });
  };
};

const updatePolicyStep = (field, value) => {
  const payload = {
    field,
    value
  };
  return {
    type: ACTION_TYPES.UPDATE_POLICY_STEP,
    payload
  };
};


/**
 * Saves the given policy to the server.
 * @param policy
 * @param callbacks
 * @public
 */
const savePolicy = (policy, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.SAVE_POLICY,
    promise: policyAPI.savePolicy(policy),
    meta: {
      onSuccess: (response) => {
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        callbacks.onFailure(response);
      }
    }
  };
};

/**
 * Saves & Publishes the given policy to the server.
 * @param policy
 * @param callbacks
 * @public
 */
const savePublishPolicy = (policy, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.SAVE_PUBLISH_POLICY,
    promise: policyAPI.savePublishPolicy(policy),
    meta: {
      onSuccess: (response) => {
        callbacks.onSuccess(response);
      },
      onFailure: (response) => {
        callbacks.onFailure(response);
      }
    }
  };
};

const discardPolicyChanges = () => {
  return {
    type: ACTION_TYPES.DISCARD_POLICY_CHANGES
  };
};


/**
 * define-policy-step...
 * This gets called when the user clicks on the plus sign in the available settings section on the left.
 * The id of the clicked object is passed and the reducer adds the entry to the selectedSettings array.
 * @param id
 * @public
 */
const addToSelectedSettings = (id) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS,
      payload: id
    });
    dispatch(_updateHeadersForAllSettings());
  };
};

/**
 * define-policy-step...
 * This gets called when the user clicks on the minus sign in the selected settings section on the right.
 * The id of the clicked object is passed and the reducer removes the entry from the selectedSettings array.
 * @param id
 * @public
 */
const removeFromSelectedSettings = (id) => {
  // if the main id like scanScheduleId gets removed, we don't want any of it's child components
  // like (effective date, recurrence interval, processor usage) be displayed in selected settings.
  // so reset the state to defaults to clear out everything in selected settings.
  return (dispatch) => {
    switch (id) {
      case scanScheduleId:
        dispatch({
          type: ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS
        });
        break;
      default:
        dispatch({
          type: ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS,
          payload: id
        });
    }
    dispatch(_updateHeadersForAllSettings());
  };
};

/**
 * define-policy-step...
 * This gets dispatched alongside with addToSelectedSettings, removeFromSelectedSettings and fetchPolicy
 * @private
 */
const _updateHeadersForAllSettings = () => {
  return {
    type: ACTION_TYPES.UPDATE_HEADERS_FOR_ALL_SETTINGS
  };
};

/**
 * Updates the policy.policyType prop and reinitializes the rest of the state
 * @param policyType
 * @public
 */
const updatePolicyType = (policyType) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.UPDATE_POLICY_TYPE,
      payload: policyType
    });
    // init type specific actions
    initializePolicyType(policyType, dispatch);
  };
};

/**
 * Updates policy prop(s) in Redux state by specifying the field name(s) (fully qualified, e.g., 'policy.name')
 * and the new value(s) that should be set
 * @param field
 * @param value
 * @public
 */
const updatePolicyProperty = (field, value) => {
  return (dispatch) => {
    let type = ACTION_TYPES.UPDATE_POLICY_PROPERTY;
    let payload = {};
    let dispatchUpdateHeadersForAllSettings = false;
    switch (field) {
      // edrPolicy specific props
      case 'scanType':
        type = ACTION_TYPES.TOGGLE_SCAN_TYPE;
        payload = value;
        dispatchUpdateHeadersForAllSettings = true;
        break;
      case 'recurrenceUnit': {
        const runOnDaysOfWeek = value === 'WEEKS' ? ['MONDAY'] : null;
        payload = [
          { field: 'policy.recurrenceUnit', value },
          // reset recurrenceInterval & runOnDaysOfWeek when toggling recurrenceUnit
          { field: 'policy.recurrenceInterval', value: 1 },
          { field: 'policy.runOnDaysOfWeek', value: runOnDaysOfWeek }
        ];
      }
        break;
      case 'primaryAddress':
        payload = [
          { field: 'policy.primaryNwServiceId', value: value.id },
          { field: 'policy.primaryAddress', value: value.host }
        ];
        break;
      // windowsLogPolicy specific props
      case 'primaryDestination':
        payload = [
          { field: 'policy.primaryDestination', value: value.host }
        ];
        break;
      case 'secondaryDestination':
        payload = [
          { field: 'policy.secondaryDestination', value: value.host }
        ];
        break;
      case 'selectedFileSource':
        payload = [
          { field: 'policy.selectedFileSource', value: value.name }
        ];
        break;
      default:
        payload = [{ field: `policy.${field}`, value }];
    }
    dispatch({ type, payload });
    if (dispatchUpdateHeadersForAllSettings) {
      dispatch(_updateHeadersForAllSettings());
    }
  };
};

/**
 * Fetches list of policies used to validate policy name uniqueness
 * @public
 */
const fetchPolicyList = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_POLICY_LIST,
      promise: policyAPI.fetchPolicyList()
    });
  };
};

// ===================================================
// edrPolicy specific action creators
// ===================================================
const fetchEndpointServers = () => {
  return {
    type: ACTION_TYPES.FETCH_ENDPOINT_SERVERS,
    promise: policyAPI.fetchEndpointServers()
  };
};

const edrDefaultPolicy = () => {
  return {
    type: ACTION_TYPES.EDR_DEFAULT_POLICY
  };
};

// ===================================================
// windowsLogPolicy specific action creators
// ===================================================
const fetchLogServers = () => {
  return {
    type: ACTION_TYPES.FETCH_LOG_SERVERS,
    promise: policyAPI.fetchLogServers()
  };
};

// ===================================================
// filePolicy specific action creators
// ===================================================
const fetchFileSourceTypes = () => {
  return {
    type: ACTION_TYPES.FETCH_FILE_SOURCE_TYPES,
    promise: policyAPI.fetchFileSourceTypes()
  };
};

/**
 * Adds the given filePolicy source into the policy.sources[] Array
 * @param {*} source
 * @public
 */
const addPolicyFileSource = (source) => {
  return {
    type: ACTION_TYPES.ADD_POLICY_FILE_SOURCE,
    payload: source
  };
};

/**
 * Removes a source by the given id/index from the policy.sources[] Array
 * @param {*} sourceId (currently the array index)
 * @public
 */
const removePolicyFileSource = (sourceId) => {
  return {
    type: ACTION_TYPES.REMOVE_POLICY_FILE_SOURCE,
    payload: sourceId
  };
};

/**
 * Updates policy.sources[sourceId] prop(s) in Redux state by specifying the field name(s) (fully qualified, e.g., 'policy.sources.sourceName')
 * and the new value(s) that should be set
 * @param sourceId (currently the array index)
 * @param field single field or a some.attr.path to be parsed and used with seamless-immutable's setIn()
 * @param value
 * @public
 */
const updatePolicyFileSourceProperty = (sourceId, field, value) => {
  return (dispatch) => {
    const type = ACTION_TYPES.UPDATE_POLICY_FILE_SOURCE_PROPERTY;
    let payload = {};
    switch (field) {
      // case 'someNonDefaultFieldOrPath':
      //   type = ACTION_TYPES.SOME_OTHER_TYPE_IF_NEEDED;
      //   payload = someOtherPayloadFormatIfNeeded;
      //   break;
      default:
        payload = [{ sourceId, field: `policy.sources.${sourceId}.${field}`, value }];
    }
    dispatch({ type, payload });
  };
};

export {
  initializePolicy,
  newPolicy,
  fetchPolicy,
  updatePolicyStep,
  savePolicy,
  savePublishPolicy,
  discardPolicyChanges,
  initializePolicyType,
  addToSelectedSettings,
  removeFromSelectedSettings,
  updatePolicyType,
  updatePolicyProperty,
  fetchPolicyList,
  // edrPolicy specific action creators
  fetchEndpointServers,
  // windowsLogPolicy specific action creators
  fetchLogServers,
  // filePolicy specific action creators
  fetchFileSourceTypes,
  addPolicyFileSource,
  removePolicyFileSource,
  updatePolicyFileSourceProperty
};
