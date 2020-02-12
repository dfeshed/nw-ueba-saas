import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'health-wellness/actions/types';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = Immutable.from({
  monitors: []
});

const hwReducer = handleActions({

  [ACTION_TYPES.GET_MONITORS]: (state, action) => {
    return handle(state, action, {
      success: (s) => {
        return s.set('monitors', action.payload.data.items);
      }
    });
  }

}, initialState);

export default hwReducer;