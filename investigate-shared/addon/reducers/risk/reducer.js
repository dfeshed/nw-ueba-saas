import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';
import fixNormalizedEvents from './util';
import { transform } from 'investigate-shared/utils/meta-util';

const riskScoreState = Immutable.from({
  isRiskScoreReset: true,
  activeRiskSeverityTab: 'critical',
  riskScoreContext: null,
  riskScoreContextError: null,
  eventsData: [],
  eventsLoadingStatus: null,
  alertsError: null,
  selectedAlert: null,
  expandedEventId: null,
  isRespondServerOffline: false
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
    return state.merge({
      riskScoreContext: null,
      riskScoreContextError: null,
      activeRiskSeverityTab: 'critical',
      eventsLoadingStatus: null,
      selectedAlert: null,
      eventsData: [],
      expandedEventId: null,
      alertsError: null
    });
  },
  [ACTION_TYPES.SET_SELECTED_ALERT]: (state, { payload }) => {
    return state.set('selectedAlert', payload.alertName);
  },
  [ACTION_TYPES.GET_EVENTS]: (state, { payload: { indicatorId, events } }) => {
    const { eventsData } = state;
    const transformedEvents = events.map(transform);
    transformedEvents.forEach((evt, index) => {
      // Tag each retrieved event with its parent indicator id.
      // This is useful downstream for mapping events back to their parent.
      evt.indicatorId = indicatorId;
      evt.eventIndex = index;

      // Ensure each event has an id.
      // This is useful for selecting individual events in the UI.
      if (!evt.id) {
        evt.id = `${indicatorId}:${index}`;
      }
    });
    return state.set('eventsData', [ ...eventsData, ...transformedEvents ]);
  },
  [ACTION_TYPES.GET_RESPOND_EVENTS]: (state, { payload: { indicatorId, events } }) => {
    const { eventsData } = state;
    events.forEach((evt, index) => {
      // Tag each retrieved event with its parent indicator id.
      // This is useful downstream for mapping events back to their parent.
      evt.indicatorId = indicatorId;
      evt.eventIndex = index;

      // Ensure each event has an id.
      // This is useful for selecting individual events in the UI.
      if (!evt.id) {
        evt.id = `${indicatorId}:${index}`;
      }
    });
    fixNormalizedEvents(events);
    return state.set('eventsData', [ ...eventsData, ...events ]);
  },
  [ACTION_TYPES.GET_RESPOND_EVENTS_INITIALIZED]: (state) => {
    return state.set('eventsLoadingStatus', 'loading');
  },
  [ACTION_TYPES.GET_RESPOND_EVENTS_COMPLETED]: (state) => {
    return state.set('eventsLoadingStatus', 'completed');
  },
  [ACTION_TYPES.CLEAR_EVENTS]: (state) => {
    return state.merge({ eventsData: [], eventsLoadingStatus: null });
  },
  [ACTION_TYPES.GET_EVENTS_COMPLETED]: (state) => {
    return state.set('eventsLoadingStatus', 'completed');
  },
  [ACTION_TYPES.GET_EVENTS_ERROR]: (state) => {
    return state.set('eventsLoadingStatus', 'error');
  },
  [ACTION_TYPES.EXPANDED_EVENT]: (state, { id }) => {
    if (state.expandedEventId === id) {
      return state.set('expandedEventId', null);
    }
    return state.set('expandedEventId', id);
  },
  [ACTION_TYPES.GET_RESPOND_SERVER_STATUS]: (state, { payload }) => {
    return state.set('isRespondServerOffline', payload);
  }
}, riskScoreState);

export default riskScoreReducer;
