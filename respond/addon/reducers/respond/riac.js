import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';

const initialState = {
  // RIAC is disabled by default until we get the actual backend value
  isRiacEnabled: undefined
};

const riacReducers = reduxActions.handleActions({
  [ACTION_TYPES.GET_RIAC_SETTINGS]: (state, action) => (
    handle(state, action, {
      start: (s) => ({ ...s }),
      success: (s) => ({ ...s, isRiacEnabled: action.payload.data.enabled })
    })
  )

}, initialState);

export default riacReducers;
