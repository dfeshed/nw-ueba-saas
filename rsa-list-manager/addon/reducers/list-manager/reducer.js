import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import { handle } from 'redux-pack';
import sort from 'fast-sort';
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
  helpId: undefined, // object for contextual help { moduleId: "investigation", topicId: "eaColumnGroups" }
  isItemsLoading: false
});

const listManagerReducer = handleActions({
  [ACTION_TYPES.INITIALIZE_LIST_MANAGER]: (state, { payload }) => {
    return state.merge({
      ...payload,
      filterText: ''
    });
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
      filterText: '',
      editItemId: undefined
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
  },
  [ACTION_TYPES.ITEM_CREATE]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.merge({
          isItemsLoading: true,
          createItemErrorCode: null,
          createItemErrorMessage: null
        });
      },
      failure: (s) => s.merge({
        isItemsLoading: false,
        createItemErrorCode: action.payload.code,
        createItemErrorMessage: action.payload.meta.message
      }),
      success: (s) => {
        const createdItem = action.payload.data;
        // add the newly created item to state
        const list = s.list ?
          sort([...s.list, createdItem]).by([{ asc: (item) => item.name.toUpperCase() }]) :
          [createdItem];
        return s.merge({
          list,
          isItemsLoading: false
        });
      }
    });
  },

  [ACTION_TYPES.ITEM_DELETE]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.merge({
          isItemsLoading: true,
          deleteItemCode: null,
          deleteItemErrorMessage: null
        });
      },
      failure: (s) => s.merge({
        isItemsLoading: false,
        deleteItemErrorCode: action.payload.code,
        deleteItemErrorMessage: action.payload.meta ? action.payload.meta.message : undefined
      }),
      success: (s) => {
        const deletedId = action.payload.request.id;
        // remove the deleted item from state
        const list = s.list.filter((item) => item.id !== deletedId);
        return s.merge({
          list,
          isItemsLoading: false,
          viewName: LIST_VIEW,
          editItemId: undefined
        });
      }
    });
  },

  [ACTION_TYPES.ITEM_UPDATE]: (state, action) => {
    return handle(state, action, {
      start: (s) => {
        return s.merge({
          isItemsLoading: true,
          updateItemErrorCode: null,
          updateItemErrorMessage: null
        });
      },
      failure: (s) => s.merge({
        isItemsLoading: false,
        updateItemErrorCode: action.payload.code,
        updateItemErrorMessage: action.payload.meta ? action.payload.meta.message : undefined
      }),
      success: (s) => {
        const updatedItem = action.payload.data;
        // TODO bhanun mapping function
        // replace the updated item by id
        const updatedIdRemoved = [...s.list].filter((item) => item.id !== updatedItem.id);
        const list = sort([...updatedIdRemoved, updatedItem]).by([{ asc: (item) => item.name.toUpperCase() }]);
        return s.merge({
          list,
          isItemsLoading: false
        });
      }
    });
  }
}, listManagerInitialState);

export default listManagerReducer;
