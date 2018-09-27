import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  data: undefined,
  reason: undefined,
  status: undefined,
  threshold: 100000
});

export default handleActions({
  [ACTION_TYPES.START_GET_EVENT_COUNT]: (state) => {
    return state.merge({
      data: undefined,
      status: 'wait',
      reason: undefined
    });
  },

  [ACTION_TYPES.FAILED_GET_EVENT_COUNT]: (state, action) => {
    return state.merge({
      status: 'rejected',
      reason: action.payload
    });
  },

  [ACTION_TYPES.EVENT_COUNT_RESULTS]: (state, action) => {
    return state.merge({
      data: action.payload.data,
      status: 'resolved',
      reason: 0
    });
  }
}, _initialState);
