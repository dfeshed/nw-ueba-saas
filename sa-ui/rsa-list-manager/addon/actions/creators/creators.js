import * as ACTION_TYPES from 'rsa-list-manager/actions/types';
import { shouldSelectedItemPersist, list } from 'rsa-list-manager/selectors/list-manager/selectors';
import { DETAILS_VIEW } from 'rsa-list-manager/constants/list-manager';

const _setSelectedItemById = (id, stateLocation) => {
  return (dispatch, getState) => {
    const canHaveSelectedItem = shouldSelectedItemPersist(getState(), stateLocation);
    // set selected item only if shouldSelectedItemPersist is true
    if (canHaveSelectedItem) {
      dispatch({
        type: ACTION_TYPES.SET_SELECTED_ITEM_ID,
        payload: id,
        meta: { belongsTo: stateLocation }
      });
    }
  };
};

/**
 *
 * @param {object} inputs { stateLocation, listName, list, selectedItemId, ... }
 */
export const initializeListManager = (inputs) => ({
  type: ACTION_TYPES.INITIALIZE_LIST_MANAGER,
  payload: inputs,
  meta: { belongsTo: inputs.stateLocation }
});

export const setHighlightedIndex = (index, stateLocation) => ({
  type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
  payload: index,
  meta: { belongsTo: stateLocation }
});

/**
 * resets highlightedIndex, filterText, viewName
 * @param {string} stateLocation e.g. 'listManagers.columnGroups'
 */
export const listVisibilityToggled = (stateLocation) => ({
  type: ACTION_TYPES.LIST_VISIBILITY_TOGGLED,
  meta: { belongsTo: stateLocation }
});

export const setFilterText = (text, stateLocation) => ({
  type: ACTION_TYPES.SET_FILTER_TEXT,
  payload: text,
  meta: { belongsTo: stateLocation }
});

export const resetFilterText = (stateLocation) => setFilterText('', stateLocation);

export const viewChanged = (viewname, stateLocation) => ({
  type: ACTION_TYPES.SET_VIEW_NAME,
  payload: viewname,
  meta: { belongsTo: stateLocation }
});

export const setSelectedItem = (item, stateLocation) => _setSelectedItemById(item.id, stateLocation);

export const beginEditItem = (editItemId, stateLocation) => {
  return (dispatch, getState) => {
    // including entire edit item as reducers outside rsa-list-manager
    // are interested in that item's contents
    const l = list(getState(), stateLocation);
    const editItem = l && editItemId ? l.find((item) => item.id === editItemId) : undefined;
    dispatch({
      type: ACTION_TYPES.EDIT_ITEM,
      payload: { editItemId, editItem },
      meta: { belongsTo: stateLocation }
    });
  };
};

export const beginCreateItem = (stateLocation) => viewChanged(DETAILS_VIEW, stateLocation);
