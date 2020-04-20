import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  statusTypes: [],
  remediationStatusTypes: [],
  alertTypes: [],
  alertSources: [],
  alertNames: [],
  milestoneTypes: []
};

export default reduxActions.handleActions({
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
      success: (s) => {
        const validNames = action.payload.data.filter((name) => name !== null);
        return s.set('alertNames', validNames);
      }
    }
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