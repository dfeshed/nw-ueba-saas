import reselect from 'reselect';
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
