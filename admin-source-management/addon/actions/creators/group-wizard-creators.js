import * as ACTION_TYPES from 'admin-source-management/actions/types';
import groupsAPI from 'admin-source-management/actions/api/groups-api';
import policyAPI from 'admin-source-management/actions/api/policy-api';
import { lookup } from 'ember-dependency-lookup';
import {
  groupRankingQuery,
  assignedPolicies,
  limitedPolicySourceTypes
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

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

const placeholderPrep = (field, key, action, previousType) => {
  // Build the placeholder
  // We only need id and name
  return (dispatch, getState) => {
    let value = { ...assignedPolicies(getState()) };
    const limitedPST = limitedPolicySourceTypes(getState());
    const i18n = lookup('service:i18n');
    const placeholderName = i18n.t('adminUsm.groupWizard.applyPolicy.policyPlaceholder').toString();
    const reference = { referenceId: 'placeholder', name: placeholderName };
    if (action === 'change') {
      delete value[previousType];
      value[key] = reference;
    } else if (action === 'add') {
      value[limitedPST[0]] = reference;
    } else { // remove and replace with placeholder
      value = reference;
    }
    const payload = {
      field,
      value
    };
    dispatch({
      type: ACTION_TYPES.EDIT_GROUP,
      payload
    });
  };
};

const updateGroupStep = (field, value) => {
  const payload = {
    field,
    value
  };
  return {
    type: ACTION_TYPES.UPDATE_GROUP_STEP,
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

const addCriteria = () => {
  return {
    type: ACTION_TYPES.ADD_CRITERIA
  };
};
const removeCriteria = (criteriaPath) => {
  const payload = {
    criteriaPath
  };
  return {
    type: ACTION_TYPES.REMOVE_CRITERIA,
    payload
  };
};

const handleAndOrOperator = (andOr) => {
  const payload = {
    andOr
  };
  return {
    type: ACTION_TYPES.ADD_OR_OPERATOR,
    payload
  };
};

const fetchGroupRanking = (sourceType) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.FETCH_GROUP_RANKING,
      promise: groupsAPI.fetchGroupRanking(sourceType),
      meta: {
        onSuccess() {
          dispatch(handleSourceType(sourceType));
        }
      }
    });
  };
};

const reorderRanking = (groupRanking) => {
  const payload = {
    groupRanking
  };
  return {
    type: ACTION_TYPES.REORDER_GROUP_RANKING,
    payload
  };
};

const resetRanking = () => {
  return {
    type: ACTION_TYPES.RESET_GROUP_RANKING
  };
};

const selectGroupRanking = (groupRankingName) => {
  const payload = {
    groupRankingName
  };
  return {
    type: ACTION_TYPES.SELECT_GROUP_RANKING,
    payload
  };
};

const setTopRanking = () => {
  return {
    type: ACTION_TYPES.SET_TOP_RANKING
  };
};

const saveGroupRanking = (callbacks = callbacksDefault) => {
  return (dispatch, getState) => {
    const query = groupRankingQuery(getState());
    dispatch({
      type: ACTION_TYPES.SAVE_GROUP_RANKING,
      promise: groupsAPI.saveGroupRanking(query),
      meta: {
        onSuccess: () => {
          callbacks.onSuccess();
        },
        onFailure: () => {
          callbacks.onFailure();
        }
      }
    });
  };
};

const handleSourceType = (sourceType) => {
  const payload = {
    sourceType
  };
  return {
    type: ACTION_TYPES.SOURCE_TYPE,
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

const removePlaceholderPolicyAssignments = () => {
  return {
    type: ACTION_TYPES.REMOVE_PLACEHOLDER_POLICY_ASSIGNMENTS
  };
};

const updateCriteriaFromCache = () => {
  return {
    type: ACTION_TYPES.UPDATE_CRITERIA_FROM_CACHE
  };
};

export {
  initializeGroup,
  fetchGroupList,
  fetchPolicyList,
  newGroup,
  fetchGroup,
  editGroup,
  updateGroupStep,
  saveGroup,
  updateGroupCriteria,
  savePublishGroup,
  addCriteria,
  removeCriteria,
  handleAndOrOperator,
  fetchGroupRanking,
  handleSourceType,
  removePlaceholderPolicyAssignments,
  updateCriteriaFromCache,
  reorderRanking,
  resetRanking,
  saveGroupRanking,
  selectGroupRanking,
  setTopRanking,
  placeholderPrep
};
