import reselect from 'reselect';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
// TODO such as _listManagers, _listName

// temporary until listName is set up
const _columnGroupsListManager = (state) => state.listManagers ? state.listManagers.columnGroups : undefined;
const _listManager = (state) => state.listManager;

// SELECTORS

// TODO update this selector to use listName
// hardcoded for Column Groups for now
export const highlightedIndex = createSelector(
  [_columnGroupsListManager, _listManager],
  (columnGroupsListManager, listManager) => {
    if (columnGroupsListManager) {
      return columnGroupsListManager.highlightedIndex;
    } else {
      if (listManager) {
        return listManager.highlightedIndex;
      }
    }
  }
);
