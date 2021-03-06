import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-shared/actions/types';
import fixNormalizedEvents from './util';
import { transform } from 'investigate-shared/utils/meta-util';
import _ from 'lodash';

const riskScoreState = Immutable.from({
  isRiskScoreReset: true,
  activeRiskSeverityTab: 'critical',
  currentEntityId: null, // agentId for host, checksumSha256 for file
  riskScoreContext: null,
  riskScoreContextError: null,
  eventContext: null,
  eventContextError: null,
  eventsData: [],
  eventsLoadingStatus: null,
  alertsError: null,
  selectedAlert: null,
  expandedEventId: null,
  isRespondServerOffline: false,
  alertsLoadingStatus: null
});

const riskScoreReducer = handleActions({

  [ACTION_TYPES.RESET_RISK_SCORE]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.set('isRiskScoreReset', true),
      failure: (s) => s.set('isRiskScoreReset', false)
    });
  },
  [ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB]: (state, { payload: { tabName } }) => {
    return state.merge({ activeRiskSeverityTab: tabName, selectedAlert: null, expandedEventId: null });
  },

  [ACTION_TYPES.GET_RISK_SCORE_CONTEXT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('alertsLoadingStatus', 'loading'),
      success: (s) => s.merge({
        riskScoreContext: action.payload.data,
        alertsLoadingStatus: 'completed'
      }),
      failure: (s) => s.merge({
        riskScoreContextError: action.payload.meta,
        alertsLoadingStatus: null
      })
    });
  },

  [ACTION_TYPES.SET_RISK_SCORE_DETAIL_CONTEXT]: (state, { payload: { data: { categorizedAlerts } } }) => {
    const alertCategory = _.upperFirst(state.activeRiskSeverityTab);
    const { eventContexts } = categorizedAlerts[alertCategory][state.selectedAlert];
    return state.set('eventContext', eventContexts);
  },

  [ACTION_TYPES.RESET_RISK_CONTEXT]: (state) => {
    return state.merge({
      riskScoreContext: null,
      riskScoreContextError: null,
      activeRiskSeverityTab: 'critical',
      currentEntityId: null,
      eventContext: null,
      eventContextError: null,
      eventsLoadingStatus: null,
      selectedAlert: null,
      eventsData: [],
      expandedEventId: null,
      alertsError: null,
      alertsLoadingStatus: null
    });
  },
  [ACTION_TYPES.SET_SELECTED_ALERT]: (state, { payload }) => {
    return state.merge({ selectedAlert: payload.alertName, expandedEventId: null });
  },
  [ACTION_TYPES.SET_CURRENT_ENTITY_ID]: (state, { payload }) => {
    return state.merge({ currentEntityId: payload.id });
  },
  [ACTION_TYPES.GET_ESA_EVENTS]: (state, { payload: { indicatorId, events } }) => {
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
  [ACTION_TYPES.GET_RESPOND_EVENTS]: (state, { payload: { events } }) => {
    const { eventsData } = state;
    const normalizedEvent = [];
    events.forEach((alertEvent) => {
      const { id, alert: { events } } = alertEvent;
      events.forEach((evt, index) => {
        // Tag each retrieved event with its parent indicator id.
        // This is useful downstream for mapping events back to their parent.
        evt.indicatorId = id;
        evt.eventIndex = index;

        // Ensure each event has an id.
        // This is useful for selecting individual events in the UI.
        if (!evt.id) {
          evt.id = `${id}:${index}`;
        }
      });
      fixNormalizedEvents(events);
      events.forEach((event) => {
        normalizedEvent.push(event);
      });
    });

    return state.set('eventsData', [ ...eventsData, ...normalizedEvent ]);
  },
  [ACTION_TYPES.GET_EVENTS_INITIALIZED]: (state) => {
    return state.set('eventsLoadingStatus', 'loading');
  },
  [ACTION_TYPES.CLEAR_EVENTS]: (state) => {
    return state.merge({ eventsData: [], eventsLoadingStatus: null });
  },
  [ACTION_TYPES.GET_EVENTS_COMPLETED]: (state) => {
    return state.set('eventsLoadingStatus', 'completed');
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
