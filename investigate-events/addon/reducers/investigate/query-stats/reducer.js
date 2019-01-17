import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  isConsoleOpen: false,
  description: null,
  percent: 0,
  errors: [],
  warnings: [],
  devices: [],
  streamingStartedTime: null, // time at which events begin loading in the browser
  streamingEndedTime: null // time at which all events have been loaded into the browser
});

export default handleActions({
  // ensure back button clears query console
  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state) => {
    return state.merge(_initialState);
  },

  [ACTION_TYPES.INITIALIZE_QUERYING]: (state) => {
    return state.merge(_initialState);
  },

  [ACTION_TYPES.DELETE_GUIDED_PILLS]: (state) => {
    return state.merge({
      isConsoleOpen: false
    });
  },

  [ACTION_TYPES.TOGGLE_QUERY_CONSOLE]: (state) => {
    return state.merge({
      isConsoleOpen: !state.isConsoleOpen
    });
  },

  [ACTION_TYPES.START_GET_EVENT_COUNT]: (state) => {
    return state.merge({
      warnings: [],
      errors: []
    });
  },

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state, { streamingStartedTime }) => {
    return streamingStartedTime ? state.merge({ streamingStartedTime }) : state;
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_STATUS]: (state, { streamingEndedTime }) => {
    return streamingEndedTime ? state.merge({ streamingEndedTime }) : state;
  },

  [ACTION_TYPES.QUERY_STATS]: (state, { payload, code }) => {
    const updatedState = {};

    if (payload.description) {
      updatedState.description = payload.description;
    }

    if (payload.percent) {
      const percent = parseInt(payload.percent, 10);
      // true completion is denoted by the presence of devices or errors
      // we can receive percent of 100 while processing is still occurring
      updatedState.percent = percent === 100 ? 99 : percent;
    }

    if (payload.devices) {
      updatedState.devices = payload.devices;
    }

    if (payload.error) {
      updatedState.errors = [{
        serviceId: payload.serviceId,
        error: payload.error
      }, ...state.errors];
    }

    if (code > 0) {
      updatedState.errors = [{
        error: payload.message
      }, ...state.errors];
    }

    if (payload.warning) {
      updatedState.warnings = [{
        serviceId: payload.serviceId,
        warning: payload.warning
      }, ...state.warnings];
    }

    return state.merge(updatedState);
  }

}, _initialState);
