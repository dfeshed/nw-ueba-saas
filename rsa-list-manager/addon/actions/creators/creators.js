import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

// TODO add more properties later
/**
 *
 * @param {object} inputs { listLocation, listName, ... }
 */
export const initializeListManager = (inputs) => ({
  type: ACTION_TYPES.INITIALIZE_LIST_MANAGER,
  payload: inputs,
  meta: { belongsTo: inputs.listLocation }
});

export const setHighlightedIndex = (index, listLocation) => ({
  type: ACTION_TYPES.SET_HIGHLIGHTED_INDEX,
  payload: index,
  meta: { belongsTo: listLocation }
});
