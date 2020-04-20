import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { MAX_EVENTS_ALLOWED } from 'investigate-events/reducers/investigate/event-results/reducer';

const _initialState = Immutable.from({
  data: undefined,
  reason: undefined,
  status: undefined,
  threshold: MAX_EVENTS_ALLOWED // default default. In case our event-settings api returns an error
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
  },
  [ACTION_TYPES.SET_MAX_EVENT_LIMIT]: (state, action) => {
    return handle(state, action, {
      failure: (s) => s, // in creators by generic handlers
      success: (s) => {
        const { calculatedEventLimit } = action.payload.data;
        return s.set('threshold', calculatedEventLimit);
      }
    });
  }
}, _initialState);
