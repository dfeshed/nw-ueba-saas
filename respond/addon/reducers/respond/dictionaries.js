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

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_CATEGORY_TAGS]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s, categoryTags: [] }),
      failure: (s) => ({ ...s, categoryTags: [] }),
      success: (s) => ({ ...s, categoryTags: action.payload.data.mapBy('parent').uniq().compact() }) }
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