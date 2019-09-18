import reselect from 'reselect';
import {
  LIST_VIEW
} from 'rsa-list-manager/constants/list-manager';

const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _rootState = (state, listLocation) => {
  let location;
  if (listLocation) {
    location = listLocation;
  } else if (state.listLocation) {
    location = state.listLocation;
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
    return !!rootState.listLocation;
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

export const selectedItem = createSelector(
  [_rootState],
  (rootState) => {
    return rootState.selectedItem;
  }
);

export const selectedIndex = createSelector(
  [filteredList, selectedItem],
  (filteredList, selectedItem) => {
    return filteredList && selectedItem ? filteredList.findIndex((item) => item.id === selectedItem.id) : -1;
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
    return filteredList && highlightedIndex > -1 ? filteredList[highlightedIndex].id : undefined;
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
