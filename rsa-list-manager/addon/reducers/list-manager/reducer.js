import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

const listManagerInitialState = Immutable.from({
  highlightedIndex: -1
});

const listManagerReducer = handleActions({
  [ACTION_TYPES.SET_HIGHLIGHTED_INDEX]: (state, action) => {
    return state.set('highlightedIndex', action.payload);
  }
}, listManagerInitialState);

export default listManagerReducer;
