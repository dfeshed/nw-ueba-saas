import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';
import policyAPI from 'admin-source-management/actions/api/policy-api';

const callbacksDefault = { onSuccess() {}, onFailure() {} };

const initializeGroup = (groupId) => {
  return (dispatch /* , getState */) => {
    // const state = getState();
    if (groupId === 'create-new') {
      dispatch(newGroup());
    } else {
      dispatch(fetchGroup(groupId));
    }

    // init group and policy lists
    dispatch(fetchGroupList());
    dispatch(fetchPolicyList());
  };
};

const fetchGroupList = () => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_GROUP_LIST,
      promise: groupsAPI.fetchGroupList()
    });
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
 * Replaces any previous group state with the template for a brand new group
 * @public
 */
const newGroup = () => ({ type: ACTION_TYPES.NEW_GROUP });

/**
 * Fetches a single group for edit
 * @public
 */
const fetchGroup = (id, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.FETCH_GROUP,
    promise: groupsAPI.fetchGroup(id),
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

const updateGroupCriteria = (criteriaPath, value, fieldIndex) => {
  const payload = {
    criteriaPath,
    value,
    fieldIndex
  };
  return {
    type: ACTION_TYPES.UPDATE_GROUP_CRITERIA,
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

const savePublishGroup = (group, callbacks = callbacksDefault) => {
  return {
    type: ACTION_TYPES.SAVE_PUBLISH_GROUP,
    promise: groupsAPI.savePublishGroup(group),
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
  fetchGroupList,
  fetchPolicyList,
  newGroup,
  fetchGroup,
  editGroup,
  saveGroup,
  updateGroupCriteria,
  savePublishGroup
};
