import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  data: undefined,
  reason: undefined,
  status: undefined,
  threshold: 100000
});

export default handleActions({
  [ACTION_TYPES.GET_EVENT_COUNT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ data: undefined, status: 'wait' }),
      failure: (s) => s.merge({ status: 'rejected', reason: action.payload.code }),
      success: (s) => s.merge({ data: action.payload.data, status: 'resolved' })
    });
  }
}, _initialState);