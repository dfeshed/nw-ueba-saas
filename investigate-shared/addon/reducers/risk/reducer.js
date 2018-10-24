import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';

const riskScoreState = Immutable.from({
  isRiskScoreReset: true,
  activeRiskSeverityTab: 'critical',
  riskScoreContext: null,
  riskScoreContextError: null,
  eventsData: [],
  eventsLoadingStatus: null,
  alertsError: null,
  selectedAlert: null,
  expandedEventId: null
});

const riskScoreReducer = handleActions({

  [ACTION_TYPES.RESET_RISK_SCORE]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('isRiskScoreReset', true),
      failure: (s) => s.set('isRiskScoreReset', false)
    });
  },
  [ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB]: (state, { payload: { tabName } }) => {
    return state.merge({ activeRiskSeverityTab: tabName, selectedAlert: null });
  },

  [ACTION_TYPES.GET_RISK_SCORE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('riskScoreContext', action.payload.data),
      failure: (s) => s.set('riskScoreContextError', action.payload.meta)
    });
  },
  [ACTION_TYPES.RESET_RISK_CONTEXT]: (state) => {
    return state.set('riskScoreContext', null);
  },
  [ACTION_TYPES.SET_SELECTED_ALERT]: (state, { payload }) => {
    return state.set('selectedAlert', payload.alertName);
  },
  [ACTION_TYPES.GET_EVENTS]: (state, { payload }) => {
    return state.merge({ eventsData: payload, eventsLoadingStatus: 'loading' });
  },
  [ACTION_TYPES.GET_EVENTS_COMPLETED]: (state) => {
    return state.set('eventsLoadingStatus', 'completed');
  },
  [ACTION_TYPES.GET_EVENTS_ERROR]: (state) => {
    return state.set('eventsLoadingStatus', 'error');
  },
  [ACTION_TYPES.EXPANDED_EVENT]: (state, { id }) => {
    if (state.expandedEventId === id) {
      return {
        ...state,
        expandedEventId: null
      };
    }
    return {
      ...state,
      expandedEventId: id
    };
  }
}, riskScoreState);

export default riskScoreReducer;
