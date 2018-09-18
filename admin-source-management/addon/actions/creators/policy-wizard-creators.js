import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const scanScheduleId = 'scanType';
const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializePolicy = (policyId) => {
  return (dispatch /* , getState */) => {
    // const state = getState();
    if (policyId === 'create-new') {
      dispatch(newPolicy());
    } else {
      dispatch(fetchPolicy(policyId));
    }

    // init policy lists
    dispatch(fetchPolicyList());
  };
};

const fetchPolicyList = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_POLICY_LIST,
      promise: policyAPI.fetchPolicyList()
    });
  };
};


/**
 * Replaces any previous policy state with the template for a brand new policy
 * @public
 */
const newPolicy = () => ({ type: ACTION_TYPES.NEW_POLICY });

/**
 * Fetches a single policy for edit
 * @public
 */
const fetchPolicy = (id, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.FETCH_POLICY,
    promise: policyAPI.fetchPolicy(id),
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
    dispatch({
      type: ACTION_TYPES.ADD_LABEL_TO_SELECTED_SETTINGS
    });
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
  switch (id) {
    case scanScheduleId:
      return {
        type: ACTION_TYPES.RESET_SCAN_SCHEDULE_TO_DEFAULTS
      };
    default:
      return {
        type: ACTION_TYPES.REMOVE_FROM_SELECTED_SETTINGS,
        payload: id
      };
  }
};

/**
 * Edits a policy prop in Redux state by specifying the field name (fully qualified, e.g., 'policy.name')
 * and the new value that should be set
 * @param field
 * @param value
 * @public
 */
const editPolicy = (field, value) => {
  const payload = {
    field,
    value
  };
  return {
    type: ACTION_TYPES.EDIT_POLICY,
    payload
  };
};

/**
 * Basically the same as editPolicy except the payload here is a nested object
 * TODO - flatten the policy settings so we can merge together with editPolicy eh!
 * @param {*} field
 * @param {*} value
 * @public
 */
const updatePolicyProperty = (field, value) => {
  let type = ACTION_TYPES.UPDATE_POLICY_PROPERTY;
  let payload = {};
  switch (field) {
    case 'scanType':
      type = ACTION_TYPES.TOGGLE_SCAN_TYPE;
      payload = value;
      break;
    case 'recurrenceUnit':
      payload = [
        { field: 'policy.recurrenceUnit', value },
        // reset recurrenceInterval & runOnDaysOfWeek when toggling recurrenceUnit
        { field: 'policy.recurrenceInterval', value: 1 },
        { field: 'policy.runOnDaysOfWeek', value: null }
      ];
      break;
    default:
      payload = [
        { field: `policy.${field}`, value }
      ];
  }
  return { type, payload };
};

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

export {
  initializePolicy,
  fetchPolicyList,
  newPolicy,
  fetchPolicy,
  addToSelectedSettings,
  removeFromSelectedSettings,
  editPolicy,
  updatePolicyProperty,
  savePolicy,
  savePublishPolicy
};
