import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

// TODO add more properties later
export const initializeListManager = (listLocation) => ({
  type: ACTION_TYPES.INITIALIZE_LIST_MANAGER,
  payload: listLocation,
  meta: { belongsTo: listLocation }
});

export const setHighlightedIndex = (index, listLocation) => ({
  type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
  payload: index,
  meta: { belongsTo: listLocation }
});
