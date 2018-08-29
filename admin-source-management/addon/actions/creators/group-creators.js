import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializeGroup = (groupId) => {
  return (dispatch /* , getState */) => {
    // const state = getState();
    if (groupId) {
      // TODO for edit
      // dispatch(getGroup(groupId));
    } else {
      dispatch(newGroup());
    }

    dispatch(initGroupFetchPolicies());
  };
};

/**
 * Fetches the policies available to be tied to a group
 * @public
 */
const initGroupFetchPolicies = () => {
  return {
    type: ACTION_TYPES.INIT_GROUP_FETCH_POLICIES,
    promise: policyAPI.fetchPolicies()
  };
};

/**
 * Replaces any previous group state with the template for a brand new group
 * @public
 */
const newGroup = () => ({ type: ACTION_TYPES.NEW_GROUP });

/**
 * Edits a group prop in Redux state by specifying the field name (fully qualified, e.g., 'group.name')
 * and the new value that should be set
 * @param field
 * @param value
 * @public
 */
const editGroup = (field, value) => {
  const payload = {
    field,
    value
  };
  return {
    type: ACTION_TYPES.EDIT_GROUP,
    payload
  };
};

const saveGroup = (group, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.SAVE_GROUP,
    promise: groupsAPI.saveGroup(group),
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
  initializeGroup,
  newGroup,
  editGroup,
  saveGroup
};
