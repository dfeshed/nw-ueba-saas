import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

// TODO belongsTo
export const setHighlightedIndex = (index) => ({
  type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
  payload: index,
  // meta: { belongsTo: listName(getState()) }
  meta: { belongsTo: 'COLUMN_GROUPS' }
});
