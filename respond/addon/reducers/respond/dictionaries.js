import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  priorityTypes: [],
  statusTypes: [],
  categoryTags: [],
  remediationStatusTypes: [],
  remediationTypes: null,
  alertTypes: [],
  alertSources: [],
  alertNames: [],
  milestoneTypes: []
};

export default reduxActions.handleActions({
  [ACTION_TYPES.FETCH_CATEGORY_TAGS]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('categoryTags', []),
      failure: (s) => s.set('categoryTags', []),
      success: (s) => s.set('categoryTags', action.payload.data) }
    )
  ),

  [ACTION_TYPES.FETCH_PRIORITY_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('priorityTypes', []),
      failure: (s) => s.set('priorityTypes', []),
      success: (s) => s.set('priorityTypes', action.payload.data) }
    )
  ),

  [ACTION_TYPES.FETCH_STATUS_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('statusTypes', []),
      failure: (s) => s.set('statusTypes', []),
      success: (s) => s.set('statusTypes', action.payload.data) }
    )
  ),
  [ACTION_TYPES.FETCH_REMEDIATION_STATUS_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('remediationStatusTypes', []),
      failure: (s) => s.set('remediationStatusTypes', []),
      success: (s) => s.set('remediationStatusTypes', action.payload) }
    )
  ),

  [ACTION_TYPES.FETCH_REMEDIATION_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('remediationTypes', null),
      failure: (s) => s.set('remediationTypes', null),
      success: (s) => s.set('remediationTypes', action.payload) }
    )
  ),

  [ACTION_TYPES.FETCH_ALERT_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('alertTypes', []),
      failure: (s) => s.set('alertTypes', []),
      success: (s) => s.set('alertTypes', action.payload) }
    )
  ),

  [ACTION_TYPES.FETCH_ALERT_SOURCES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('alertSources', []),
      failure: (s) => s.set('alertSources', []),
      success: (s) => s.set('alertSources', action.payload) }
    )
  ),

  [ACTION_TYPES.FETCH_ALERT_NAMES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('alertNames', []),
      failure: (s) => s.set('alertNames', []),
      success: (s) => s.set('alertNames', action.payload.data) }
    )
  ),

  [ACTION_TYPES.FETCH_MILESTONE_TYPES]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('milestoneTypes', []),
      failure: (s) => s.set('milestoneTypes', []),
      success: (s) => s.set('milestoneTypes', action.payload.data) }
    )
  )
}, Immutable.from(initialState));