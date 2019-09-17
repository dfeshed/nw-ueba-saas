import * as ACTION_TYPES from 'rsa-list-manager/actions/types';

// TODO add more properties later
/**
 *
 * @param {object} inputs { listLocation, listName, list, ... }
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

/**
 * toggles isExpanded and resets highlightedIndex, filterText
 * @param {string} listLocation e.g. 'listManagers.columnGroups'
 */
export const toggleListVisibility = (listLocation) => ({
  type: ACTION_TYPES.TOGGLE_LIST_VISIBILITY,
  meta: { belongsTo: listLocation }
});

export const setFilterText = (text, listLocation) => ({
  type: ACTION_TYPES.SET_FILTER_TEXT,
  payload: text,
  meta: { belongsTo: listLocation }
});
