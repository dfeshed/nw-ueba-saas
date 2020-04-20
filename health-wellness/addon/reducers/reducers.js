import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'health-wellness/actions/types';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = Immutable.from({
  monitors: [],
  isError: false,
  isMonitorLoading: true
});

const hwReducer = handleActions({

  [ACTION_TYPES.GET_MONITORS]: (state, action) => {
    return handle(state, action, {
      success: (s) => s.merge({
        isMonitorLoading: false,
        monitors: action.payload.data
      }),
      failure: (s) => s.merge({
        isMonitorLoading: false,
        isError: true
      })
    });
  }

}, initialState);

export default hwReducer;