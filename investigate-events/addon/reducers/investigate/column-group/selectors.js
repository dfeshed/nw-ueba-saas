import reselect from 'reselect';
import { isEmpty } from '@ember/utils';
const { createSelector } = reselect;

// ACCESSOR FUNCTIONS
export const columnGroups = (state) => state.investigate.columnGroup.columnGroups;

// SELECTOR FUNCTIONS
export const hasColumnGroups = createSelector(
  columnGroups, (columnGroups = []) => !isEmpty(columnGroups)
);
