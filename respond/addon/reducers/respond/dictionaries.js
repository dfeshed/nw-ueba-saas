import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  priorityTypes: [],
  statusTypes: [],
  categoryTags: [],
  remediationStatusTypes: [],
  remediationTypes: null
};

/**
 * Converts the category-tag values (an array of objects with a name and parent name), into an array of
 * groups with the members of the group set as an array of options on the group. This conforms with the
 * expected model for ember-power-select group format. Cf http://www.ember-power-select.com/docs/groups
 * @param categories
 * @returns {Array}
 * @private
 */
function _constructCategoryGroups(categories) {
  const groupsByName = categories.reduce((previousValue, category) => {
    const { parent } = category;

    if (!previousValue[parent]) {
      previousValue[parent] = [];
    }
    previousValue[parent].push(category);
    return previousValue;
  }, {});

  const groups = Object.keys(groupsByName).map((groupName) => {
    return {
      groupName,
      options: groupsByName[groupName]
    };
  });

  return groups;
}

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_CATEGORY_TAGS]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, categoryTags: [] }),
      failure: (s) => ({ ...s, categoryTags: [] }),
      success: (s) => ({ ...s, categoryTags: _constructCategoryGroups(action.payload.data) }) }
    )
  ),

  [ACTION_TYPES.FETCH_PRIORITY_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, priorityTypes: [] }),
      failure: (s) => ({ ...s, priorityTypes: [] }),
      success: (s) => ({ ...s, priorityTypes: action.payload.data }) }
    )
  ),

  [ACTION_TYPES.FETCH_STATUS_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, statusTypes: [] }),
      failure: (s) => ({ ...s, statusTypes: [] }),
      success: (s) => ({ ...s, statusTypes: action.payload.data }) }
    )
  ),
  [ACTION_TYPES.FETCH_REMEDIATION_STATUS_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, remediationStatusTypes: [] }),
      failure: (s) => ({ ...s, remediationStatusTypes: [] }),
      success: (s) => ({ ...s, remediationStatusTypes: action.payload }) }
    )
  ),

  [ACTION_TYPES.FETCH_REMEDIATION_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, remediationTypes: null }),
      failure: (s) => ({ ...s, remediationTypes: null }),
      success: (s) => ({ ...s, remediationTypes: action.payload }) }
    )
  )
}, initialState);