import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';

const _initialState = Immutable.from({
  data: null,
  isLoading: null,
  isError: null
});

export default handleActions({
  [ACTION_TYPES.INITIALIZE]: (state, { payload }) => {
    return _initialState.merge(payload.services);
  },

  [ACTION_TYPES.SERVICES_RETRIEVE]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('isLoading', true),
      failure: (s) => s.merge({
        isError: true,
        isLoading: false
      }),
      success: (s) => s.merge({
        data: action.payload.data,
        isLoading: false
      })
    });
  }
}, _initialState);
