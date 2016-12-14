import Ember from 'ember';
import * as ACTION_TYPES from 'sa/actions/live-content/types';
import reduxActions from 'npm:redux-actions';

const { copy, isPresent } = Ember;

const initialState = { };

const selections = reduxActions.handleActions({
  [ACTION_TYPES.RESOURCE_TOGGLE_SELECT]: (state, { item = {} }) => {
    if (item.id) {
      state = copy(state);
      if (isPresent(state[item.id])) {
        delete state[item.id];
      } else {
        state[item.id] = item;
      }
    }
    return state;
  }
}, initialState);

export default selections;