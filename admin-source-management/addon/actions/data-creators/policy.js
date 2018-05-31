import * as ACTION_TYPES from 'admin-source-management/actions/types';
import policyAPI from 'admin-source-management/actions/api/policy';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializePolicy = (policyId) => {
  return (dispatch) => {
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

const updatePolicyProperty = (key, value) => {
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

const fetchPolicyList = () => ({
  type: ACTION_TYPES.FETCH_POLICY_LIST,
  promise: policyAPI.fetchPolicy()
});

export {
  initializePolicy,
  fetchPolicyList,
  newPolicy,
  updatePolicyProperty,
  editPolicy,
  savePolicy
};
