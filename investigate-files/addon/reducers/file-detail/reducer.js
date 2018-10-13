import { handleActions } from 'redux-actions';
import Immutable from 'seamless-immutable';

import * as ACTION_TYPES from 'investigate-files/actions/types';

const initialState = Immutable.from({
  eventsData: null,
  eventsLoadingStatus: null,
  alertsError: null,
  selectedAlert: null,
  expandedEventId: null
});

const fileDetailsState = handleActions({
  [ACTION_TYPES.GET_EVENTS]: (state, { payload }) => {
    return state.merge({ eventsData: payload, eventsLoadingStatus: 'loading' });
  },
  [ACTION_TYPES.SET_SELECTED_ALERT]: (state, { payload }) => {
    return state.set('selectedAlert', payload.alertName);
  },
  [ACTION_TYPES.GET_EVENTS_COMPLETED]: (state) => {
    return state.set('eventsLoadingStatus', 'completed');
  },
  [ACTION_TYPES.GET_EVENTS_ERROR]: (state) => {
    return state.set('eventsLoadingStatus', 'error');
  },
  [ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB]: (state) => {
    return state.set('selectedAlert', null);
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

}, initialState);

export default fileDetailsState;