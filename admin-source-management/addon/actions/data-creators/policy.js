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
  editPolicy,
  savePolicy
};
