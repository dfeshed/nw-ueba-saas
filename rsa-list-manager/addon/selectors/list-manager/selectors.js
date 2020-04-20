import reselect from 'reselect';
import {
  LIST_VIEW,
  DETAILS_VIEW
} from 'rsa-list-manager/constants/list-manager';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _rootState = (state, stateLocation) => {
  let location;
  if (stateLocation) {
    location = stateLocation;
  } else if (state.stateLocation) {
    location = state.stateLocation;
  } else {
    location = undefined;
  }

  if (location) {
    const locationSplit = location.split('.');
    let obj = state;
    for (let i = 0; i < locationSplit.length; i++) {
      obj = obj[locationSplit[i]];
      if (!obj) {
        // nested obj does not exist
        return false;
      }
    }
    return obj;
  } else {
    return state;
  }
};

// SELECTORS
const _selectedItemName = createSelector(
  _rootState,
  (rootState) => {
    if (rootState.list && rootState.selectedItemId) {
      const selectedItem = rootState.list.find((item) => item.id === rootState.selectedItemId);
      return selectedItem ? selectedItem.name : undefined;
    }
    return undefined;
  }
);

export const editItemId = createSelector(
  _rootState,
  (rootState) => {
    return rootState.editItemId;
  }
);

export const highlightedIndex = createSelector(
  _rootState,
  (rootState) => {
    return rootState.highlightedIndex;
  }
);

export const listName = createSelector(
  _rootState,
  (rootState) => {
    return rootState.listName;
  }
);

export const modelName = createSelector(
  _rootState,
  (rootState) => {
    return rootState.modelName;
  }
);

export const list = createSelector(
  _rootState,
  (rootState) => {
    return rootState.list;
  }
);

export const filteredList = createSelector(
  [_rootState],
  (rootState) => {
    if (rootState.list && rootState.filterText) {
      return rootState.list.filter((item) => item.name.toLowerCase().includes(rootState.filterText.toLowerCase()));
    } else if (rootState.list) {
      return rootState.list;
    }
  }
);

export const filterText = createSelector(
  _rootState,
  (rootState) => {
    return rootState.filterText;
  }
);

export const isListManagerReady = createSelector(
  [_rootState],
  (rootState) => {
    return !!rootState.stateLocation && !!rootState.list;
  }
);

export const itemType = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.listName ? rootState.listName.slice(0, -1) : undefined;
  }
);

export const newItemButtonTitle = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.listName ? `New ${rootState.listName.slice(0, -1)}` : undefined;
  }
);

export const isExpanded = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.isExpanded;
  }
);

export const shouldSelectedItemPersist = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.shouldSelectedItemPersist;
  }
);

export const selectedItemId = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.selectedItemId;
  }
);

export const selectedIndex = createSelector(
  [filteredList, selectedItemId],
  (filteredList, selectedItemId) => {
    return filteredList && selectedItemId ? filteredList.findIndex((item) => item.id === selectedItemId) : -1;
  }
);

export const viewName = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.viewName;
  }
);

export const highlightedId = createSelector(
  [filteredList, highlightedIndex],
  (filteredList, highlightedIndex) => {
    return filteredList?.length && highlightedIndex > -1 ? filteredList[highlightedIndex]?.id : undefined;
  }
);

export const isListView = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.viewName === LIST_VIEW;
  }
);

export const noResultsMessage = createSelector(
  [listName],
  (listName) => {
    return `All ${listName.toLowerCase()} have been excluded by the current filter`;
  }
);

export const helpId = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.helpId;
  }
);

export const hasContextualHelp = createSelector(
  [helpId],
  (helpId) => {
    if (helpId) {
      const { moduleId, topicId } = helpId;
      return !!(moduleId && topicId);
    }
    return false;
  }
);

export const caption = createSelector(
  [listName, _selectedItemName],
  (listName, selectedItemName) => {
    if (!listName) {
      return '';
    }
    // If there is selectedItemId for listName e.g "My Items" (string ending with s(plural))
    // caption will be "My Item: name of selectedItem"
    return selectedItemName ? `${listName.slice(0, -1)}: ${selectedItemName}` : listName;
  }
);

export const disabledListName = createSelector(
  [listName],
  (listName) => {
    return listName ? listName.toLowerCase() : '';
  }
);

export const titleTooltip = createSelector(
  [_selectedItemName],
  (selectedItemName) => {
    return selectedItemName;
  }
);

export const filterPlaceholder = createSelector(
  [listName],
  (listName) => {
    return `Filter ${listName.toLowerCase()}`;
  }
);

export const hasIsEditableIndicators = createSelector(
  [filteredList],
  (filteredList) => {
    const editableIndicatedItems = filteredList ? filteredList.filter((item) => typeof item.isEditable !== 'undefined') : [];
    return editableIndicatedItems.length > 0;
  }
);

export const editItem = createSelector(
  [list, editItemId],
  (list, editItemId) => {
    return list && editItemId ? list.find((item) => item.id === editItemId) : undefined;
  }
);

export const isEditable = createSelector(
  [editItem],
  (editItem) => {
    return list && editItem ? editItem.isEditable : undefined;
  }
);

export const isNewItem = createSelector(
  [viewName, editItemId],
  (viewName, editItemId) => {
    return viewName === DETAILS_VIEW && !editItemId;
  }
);

export const editItemIsSelected = createSelector(
  [list, selectedItemId, editItemId],
  (list, selectedItemId, editItemId) => {
    return list && selectedItemId && editItemId ? selectedItemId === editItemId : false;
  }
);

export const isItemsLoading = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.isItemsLoading;
  }
);
