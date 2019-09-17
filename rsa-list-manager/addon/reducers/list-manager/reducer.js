import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

const listManagerInitialState = Immutable.from({
  listLocation: undefined,
  listName: undefined,
  isExpanded: false,
  list: undefined,
  filterText: undefined,
  highlightedIndex: -1
});

const listManagerReducer = handleActions({
  // TODO add more properties later
  [ACTION_TYPES.INITIALIZE_LIST_MANAGER]: (state, { payload }) => {
    return state.merge({ ...payload });
  },
  [ACTION_TYPES.TOGGLE_LIST_VISIBILITY]: (state) => {
    return state.merge({
      isExpanded: !state.isExpanded,
      highlightedIndex: -1,
      filterText: ''
    });
  },
  [ACTION_TYPES.SET_HIGHLIGHTED_INDEX]: (state, action) => {
    return state.set('highlightedIndex', action.payload);
  },
  [ACTION_TYPES.SET_FILTER_TEXT]: (state, action) => {
    return state.merge({
      highlightedIndex: -1,
      filterText: action.payload
    });
  }
}, listManagerInitialState);

export default listManagerReducer;
