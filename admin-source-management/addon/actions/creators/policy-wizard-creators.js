import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const scanScheduleId = 'schedOrManScan';
const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializePolicy = (policyId) => {
  return (dispatch /* , getState */) => {
    // const state = getState();
    if (policyId) {
      // TODO for edit
    } else {
      dispatch(newPolicy());
    }
  };
};

/**
 * Replaces any previous policy state with the template for a brand new policy
 * @public
 */
const newPolicy = () => ({ type: ACTION_TYPES.NEW_POLICY });

/**
 * define-policy-step...
 * This gets called when the user clicks on the plus sign in the available settings section on the left.
 * The id of the clicked object is passed and the reducer adds the entry to the selectedSettings array.
 * @param id
 * @public
 */
const addToSelectedSettings = (id) => {
  return {
    type: ACTION_TYPES.ADD_TO_SELECTED_SETTINGS,
    payload: id
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
 * @param {*} key
 * @param {*} value
 * @public
 */
const updatePolicyProperty = (key, value) => {
  if (key === 'scanType') {
    return {
      type: ACTION_TYPES.TOGGLE_SCAN_TYPE,
      payload: value
    };
  }
  let payload = {};
  if (key === 'enabledScheduledScan') {
    payload = {
      scheduleConfig: {
        enabledScheduledScan: value
      }
    };
  } else if (key === 'cpuMaximum' || key === 'cpuMaximumOnVirtualMachine') {
    payload = {
      scheduleConfig: {
        scanOptions: {
          [key]: value
        }
      }
    };
  } else if (key === 'recurrenceIntervalUnit') {
    payload = {
      scheduleConfig: {
        scheduleOptions: {
          recurrenceIntervalUnit: value,
          recurrenceInterval: 1
        }
      }
    };
  } else {
    payload = {
      scheduleConfig: {
        scheduleOptions: {
          [key]: value
        }
      }
    };
  }
  return { type: ACTION_TYPES.UPDATE_POLICY_PROPERTY, payload };
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

export {
  initializePolicy,
  newPolicy,
  addToSelectedSettings,
  removeFromSelectedSettings,
  editPolicy,
  updatePolicyProperty,
  savePolicy
};
