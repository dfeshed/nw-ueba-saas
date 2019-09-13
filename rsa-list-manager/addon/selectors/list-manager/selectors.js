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

export const isListManagerReady = createSelector(
  [_rootState],
  (rootState) => {
    return !!rootState.listLocation;
  }
);
