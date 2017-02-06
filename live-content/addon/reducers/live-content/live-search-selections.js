import Ember from 'ember';
import * as ACTION_TYPES from 'live-content/actions/live-content/types';
import reduxActions from 'redux-actions';

const { copy, isPresent } = Ember;

const initialState = { };

const selections = reduxActions.handleActions({
  [ACTION_TYPES.RESOURCE_TOGGLE_SELECT]: (state, { payload = {} }) => {
    if (payload.id) {
      state = copy(state);
      if (isPresent(state[payload.id])) {
        delete state[payload.id];
      } else {
        state[payload.id] = payload;
      }
    }
    return state;
  }
}, initialState);

export default selections;