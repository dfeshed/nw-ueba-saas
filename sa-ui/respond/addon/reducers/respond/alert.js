import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';
import fixNormalizedEvents from './util/events';

const localStorageKey = 'rsa::nw::respond::alert';

let initialState = {
  // id of the alert that owns `info` & `events`
  id: null,

  // alert details
  info: null,

  // either 'streaming', 'complete' or 'error'
  infoStatus: null,

  // function to abort the info stream
  stopInfoStream: null,

  // alert events
  events: null,

  // either 'wait', 'error' or 'success'
  eventsStatus: null,

  // width of the alert inspector UI, in pixels
  inspectorWidth: 400,

  // either 'wait', 'error', or 'complete'
  originalAlertStatus: null,

  // the original (raw) alert details
  originalAlert: null
};

// Load local storage values and incorporate into initial state
initialState = load(initialState, localStorageKey);

// Mechanism to persist some of the state to local storage
// This function will curry a given reducer (function), enabling it to persist its resulting state to a given
// local storage key.
// Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
// sophisticated solution with local storage
// @param {function} callback A reducer that will update a given state before persisting it to local storage.
// @returns {Function} The curried reducer.
const persistAlertState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { inspectorWidth } = state;
    persist({ inspectorWidth }, localStorageKey);
    return state;
  });
};

const alertReducers = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_ALERT]: (state, { payload }) => {
    return state.merge({
      // reset state for a new alert id, even if it matches the old alert id,
      // because we don't want to reuse info, we want to reload it in case it may have changed on server
      id: payload,

      // there are some visual properties (not server data) properties which should be preserved
      // they should not be reset to initialState for every incident
      inspectorWidth: state.inspectorWidth || initialState.inspectorWidth
    });
  },

  [ACTION_TYPES.RESIZE_ALERT_INSPECTOR]: persistAlertState((state, { payload }) => {
    return state.set('inspectorWidth', payload);
  }),

  [ACTION_TYPES.FETCH_ALERT_DETAILS_STARTED]: (state) => {
    return state.merge({
      info: null,
      infoStatus: 'streaming'
    });
  },

  [ACTION_TYPES.FETCH_ALERT_DETAILS_STREAM_INITIALIZED]: (state, { payload }) => state.set('stopInfoStream', payload),

  [ACTION_TYPES.FETCH_ALERT_DETAILS_RETRIEVE_BATCH]: (state, { payload: { data } }) => state.set('info', data && data[0]),

  [ACTION_TYPES.FETCH_ALERT_DETAILS_COMPLETED]: (state) => {
    return state.merge({
      infoStatus: 'complete',
      stopInfoStream: null
    });
  },

  [ACTION_TYPES.FETCH_ALERT_DETAILS_ERROR]: (state) => {
    return state.merge({
      infoStatus: 'error',
      stopInfoStream: null
    });
  },

  [ACTION_TYPES.FETCH_ALERT_EVENTS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ events: null, eventsStatus: 'wait' }),
      failure: (s) => s.set('eventsStatus', 'error'),
      success: (s) => s.merge({ events: fixNormalizedEvents(action.payload.data), eventsStatus: 'success' })
    });
  },

  [ACTION_TYPES.FETCH_ORIGINAL_ALERT]: (state, action) => (
    handle(state, action, {
      start: (s) => s.merge({ originalAlertStatus: 'wait', originalAlert: null }),
      success: (s) => s.merge({
        originalAlert: action.payload.data,
        originalAlertStatus: 'complete'
      }),
      failure: (s) => s.set('originalAlertStatus', 'error')
    })
  )
}, Immutable.from(initialState));

export default alertReducers;