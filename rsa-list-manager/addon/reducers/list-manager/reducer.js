import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import {
  LIST_VIEW,
  EDIT_VIEW
} from 'rsa-list-manager/constants/list-manager';

const listManagerInitialState = Immutable.from({
  stateLocation: undefined,
  listName: undefined,
  isExpanded: false,
  list: undefined,
  filterText: undefined,
  highlightedIndex: -1,
  editItemId: undefined,
  viewName: undefined, // View to be rendered through button actions (list-view, detail-view, etc)
  selectedItemId: undefined, // id of object to identify an item as selected in the manager's button caption,
  helpId: undefined // object for contextual help { moduleId: "investigation", topicId: "eaColumnGroups" }
});

const listManagerReducer = handleActions({
  [ACTION_TYPES.INITIALIZE_LIST_MANAGER]: (state, { payload }) => {
    return state.merge({ ...payload });
  },
  [ACTION_TYPES.TOGGLE_LIST_VISIBILITY]: (state) => {
    return state.merge({
      isExpanded: !state.isExpanded,
      highlightedIndex: -1,
      filterText: '',
      viewName: LIST_VIEW
    });
  },
  [ACTION_TYPES.SET_VIEW_NAME]: (state, action) => {
    return state.merge({
      viewName: action.payload,
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
  },
  [ACTION_TYPES.SET_SELECTED_ITEM_ID]: (state, action) => {
    return state.set('selectedItemId', action.payload);
  },
  [ACTION_TYPES.EDIT_ITEM]: (state, action) => {
    return state.merge({
      editItemId: action.payload,
      viewName: EDIT_VIEW
    });
  }
}, listManagerInitialState);

export default listManagerReducer;
