import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  isConsoleOpen: false,
  description: null,
  percent: 0,
  errors: [],
  warnings: [],
  devices: []
});

export default handleActions({
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

  [ACTION_TYPES.QUERY_STATS]: (state, { payload }) => {
    const updatedState = {};

    if (payload.description) {
      updatedState.description = payload.description;
    }
    if (payload.percent) {
      updatedState.percent = parseInt(payload.percent, 10);
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

    if (payload.code > 0) {
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
