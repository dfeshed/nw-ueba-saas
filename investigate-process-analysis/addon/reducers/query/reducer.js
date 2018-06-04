import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-process-analysis/actions/types';
import reduxActions from 'redux-actions';

const initialState = {
  serviceId: null,
  startTime: null,
  endTime: null
};

export default reduxActions.handleActions({

  [ACTION_TYPES.SERVICE_SELECTED]: (state, { payload }) => {
    return state.set('serviceId', payload);
  },
  [ACTION_TYPES.SET_QUERY_TIME_RANGE]: (state, { payload }) => {
    return state.merge({ startTime: payload.startTime, endTime: payload.endTime });
  },
  [ACTION_TYPES.SET_PROCESS_ANALYSIS_INPUT]: (state, { payload }) => {
    return state.merge({ serviceId: payload.sid, startTime: payload.st, endTime: payload.et });
  }

}, Immutable.from(initialState));
