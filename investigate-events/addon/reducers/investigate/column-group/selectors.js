import reselect from 'reselect';
import { isEmpty } from '@ember/utils';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
const _originalColumnGroups = (state) => state.investigate.columnGroup.columnGroups;

const _updatedColumnGroups = (state) => state.listManagers?.columnGroups?.list;

export const columnGroups = createSelector(
  [_originalColumnGroups, _updatedColumnGroups],
  (originalColumnGroups = [], updatedColumnGroups) => {

    // refer to listManager columnGroups for the most updated list
    if (updatedColumnGroups) {
      return updatedColumnGroups;
    }
    // return originalColumnGroups only if listManager state is not updated with columnGroups yet
    return originalColumnGroups;
  }
);

// SELECTOR FUNCTIONS
export const hasColumnGroups = createSelector(
  columnGroups, (columnGroups = []) => !isEmpty(columnGroups)
);
