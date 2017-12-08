import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';
import { toggle } from 'respond/utils/immut/array';
import { isEmberArray } from 'ember-array/utils';

const localStorageKey = 'rsa::nw::respond::incident';

let initialState = {
  // id of the incident that owns `info` & `storyline`
  id: null,

  // incident details
  info: null,

  // either 'wait', 'error' or 'completed'
  infoStatus: null,

  // either 'overview', 'storyline' or 'events'
  viewMode: 'overview',

  // width of the incident details inspector UI, in pixels
  inspectorWidth: 400,

  // currently selected data in the storyline
  selection: {
    type: '', // either 'storyPoint', 'event', 'node' or 'link'; possibly empty
    ids: [] // array of ids; possibly empty
  },

  // toggles between a viz view & a data view
  hideViz: false,

  tasks: [],

  // can be 'wait', 'complete', 'creating'
  tasksStatus: null,

  isShowingTasksAndJournal: false,

  // 'journal', 'remediation' or 'search'
  tasksJournalMode: 'journal',

  // the { type, id } of the entity for the `searchResults` filter
  searchEntity: null,

  // identifier name of the time frame for the `searchResults` filter
  searchTimeFrameName: null,

  // status of the fetch for `searchResults`; either 'streaming', 'complete' or 'error'
  searchStatus: null,

  // the list of found indicators (alerts) that mention `searchEntity` within `searchTimeFrameName`
  searchResults: null,

  // identifier name of the default time range for the search UI
  defaultSearchTimeFrameName: 'LAST_TWENTY_FOUR_HOURS',

  // identifier of the default entity type for the search UI
  defaultSearchEntityType: 'IP',

  // status of the call to add a given set of related indicators to the current event
  // either 'wait', 'success' or 'error'
  addRelatedIndicatorsStatus: null
};

// Load local storage values and incorporate into initial state
initialState = load(initialState, localStorageKey);

// Mechanism to persist some of the state to local storage
// This function will curry a given reducer (function), enabling it to persist its resulting state to a given
// local storage key.
// Note: this implementation may be replaced either with (a) user preference service calls, or (b) with a more
// sophisticated solution with local storage
// @param {function} callback A reducer that will update a given state before persisting it to local storage.
// @returns {Function} The curried reducer.
const persistIncidentState = (callback) => {
  return (function() {
    const state = callback(...arguments);
    const { viewMode, inspectorWidth, hideViz, tasksJournalMode, isShowingTasksAndJournal, defaultSearchTimeFrameName, defaultSearchEntityType } = state;
    persist({ viewMode, inspectorWidth, hideViz, tasksJournalMode, isShowingTasksAndJournal, defaultSearchTimeFrameName, defaultSearchEntityType }, localStorageKey);
    return state;
  });
};

// Updates the state value with the value updated on the server.
// If the updated incidents includes the incident currently in the incident details route,
// we must update that incident's info as well.
const _handleUpdates = (action) => {
  return (state) => {
    const { payload } = action;
    // The payload can come in as an array (when multiple requests are being settled) or as an object (normal promise response)
    // To make things easier, we normalize an object payload into the format of the array payload
    const [{ value: { data } }] = !isEmberArray(payload) ? [{ value: { ...payload } }] : payload;

    return state.set('info', data[0]);
  };
};

const incident = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => {
    return state.merge({
      ...initialState,
      // reset state for a new incident id, even if it matches the old incident id,
      // because we don't want to reuse info, we want to reload it in case it may have changed on server
      id: payload,

      // there are some visual properties (not server data) properties which should be preserved
      // they should not be reset to initialState for every incident
      inspectorWidth: state.inspectorWidth || initialState.inspectorWidth,
      viewMode: state.viewMode || initialState.viewMode,
      isShowingTasksAndJournal: state.isShowingTasksAndJournal,
      tasksJournalMode: state.tasksJournalMode || initialState.tasksJournalMode,
      defaultSearchTimeFrameName: state.defaultSearchTimeFrameName || initialState.defaultSearchTimeFrameName,
      defaultSearchEntityType: state.defaultSearchEntityType || initialState.defaultSearchEntityType,
      hideViz: state.hideViz
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_DETAILS]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ info: null, infoStatus: 'wait' }),
      failure: (s) => s.set('infoStatus', 'error'),
      success: (s) => s.merge({ info: action.payload.data, infoStatus: 'completed' })
    });
  },

  [ACTION_TYPES.SET_VIEW_MODE]: persistIncidentState((state, { payload }) => {
    return state.set('viewMode', payload);
  }),

  [ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR]: persistIncidentState((state, { payload }) => {
    return state.set('inspectorWidth', payload);
  }),

  [ACTION_TYPES.SET_HIDE_VIZ]: persistIncidentState((state, { payload }) => {
    return state.set('hideViz', payload);
  }),

  [ACTION_TYPES.CLEAR_INCIDENT_SELECTION]: (state) => {
    return state.set('selection', { type: '', ids: [] });
  },

  [ACTION_TYPES.SET_INCIDENT_SELECTION]: (state, { payload: { type, id } }) => {
    const { selection: { type: wasType, ids: wasIds } } = state;
    let newSelection;
    if (wasType !== type) {
      // type has changed, so reset selection to given inputs
      newSelection = { type, ids: id ? [ id ] : [] };
    } else {
      // type hasn't changed
      // was the given id already the only selection? if so toggle it, otherwise reset to it
      const wasAlreadyOnlySelection = wasIds && (wasIds.length === 1) && (wasIds[0] === id);
      newSelection = { type, ids: wasAlreadyOnlySelection ? [] : [ id ] };
    }
    return state.set('selection', newSelection);
  },

  [ACTION_TYPES.TOGGLE_INCIDENT_SELECTION]: (state, { payload: { type, id } }) => {
    const { selection: { type: wasType, ids: wasIds } } = state;
    let newSelection;
    if (wasType !== type) {
      // type has changed, so reset selection to given inputs
      newSelection = { type, ids: id ? [ id ] : [] };
    } else {
      // type hasn't changed
      newSelection = { type, ids: toggle(wasIds, id) };
    }
    return state.set('selection', newSelection);
  },

  [ACTION_TYPES.UPDATE_INCIDENT]: (state, action) => {
    return handle(state, action, {
      success: _handleUpdates(action)
    });
  },

  [ACTION_TYPES.FETCH_REMEDIATION_TASKS_FOR_INCIDENT]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.merge({ tasks: [], tasksStatus: 'wait' }),
      failure: (s) => s.set('tasksStatus', 'error'),
      success: (s) => s.merge({ tasks: action.payload.data, tasksStatus: 'completed' })
    });
  },

  [ACTION_TYPES.CREATE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => s.set('tasksStatus', 'creating'),
      failure: (s) => s,
      success: (s) => s.set('tasks', [action.payload.data, ...s.tasks]),
      finish: (s) => s.set('tasksStatus', null)
    });
  },

  [ACTION_TYPES.DELETE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => s,
      failure: (s) => s,
      success: (s) => {
        const { payload } = action;
        let removedItemIds = [];
        // If the payload is an array, we had multiple promises (deletion requests) being settled, each of which has its own payload/resolved value
        if (isEmberArray(payload)) {
          removedItemIds = payload.reduce((removed, { value: { data } }) => removed.concat(data), []);
        } else { // a single promise (deletion request) resolved
          removedItemIds = payload.data;
        }
        // Filter out newly deleted items from the main items array
        const updatedItems = state.tasks.filter((item) => (!removedItemIds.includes(item.id)));
        return s.set('tasks', updatedItems);
      }
    });
  },

  [ACTION_TYPES.UPDATE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => s,
      failure: (s) => s,
      success: (s) => {
        const { payload } = action;
        const normalizedPayload = !isEmberArray(payload) ? [{ value: { ...payload } }] : payload;

        // The array of entities that have been updated reduced to a single array
        const updateData = normalizedPayload.reduce((updatedEntities, { value: { data } }) => {
          return updatedEntities.concat(data);
        }, []);

        const updatedEntities = state.tasks.map((entity) => {
          // Find the updated entity from the list of updated objects, or use the current entity if not updated
          return updateData.findBy('id', entity.id) || entity;
        });
        return s.set('tasks', updatedEntities);
      }
    });
  },

  [ACTION_TYPES.SET_TASKS_JOURNAL_MODE]: persistIncidentState((state, { payload }) => {
    return state.set('tasksJournalMode', payload || 'journal');
  }),

  [ACTION_TYPES.TOGGLE_TASKS_JOURNAL]: persistIncidentState((state) => {
    return state.set('isShowingTasksAndJournal', !state.isShowingTasksAndJournal);
  }),

  [ACTION_TYPES.CREATE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => s,
      failure: (s) => s,
      success: (s) => {
        const { payload: { data } } = action;
        return s.set('info', {
          ...s.info,
          notes: [...notes, data]
        });
      }
    });
  },

  [ACTION_TYPES.DELETE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => s,
      failure: (s) => s,
      success: (s) => {
        const { payload: { request: { journalId } } } = action;
        return s.set('info', {
          ...s.info,
          notes: notes.filter((note) => (note.id !== journalId))
        });
      }
    });
  },

  [ACTION_TYPES.UPDATE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => s,
      failure: (s) => s,
      success: (s) => {
        const { payload: { request: { journalId, journalMap } } } = action;
        return s.set('info', {
          ...s.info,
          notes: notes.map((note) => {
            if (note.id !== journalId) {
              return note;
            }
            return {
              ...note,
              ...journalMap
            };
          })
        });
      }
    });
  },

  [ACTION_TYPES.SET_DEFAULT_SEARCH_TIME_FRAME_NAME]: persistIncidentState((state, { payload }) => {
    return state.set('defaultSearchTimeFrameName', payload);
  }),

  [ACTION_TYPES.SET_DEFAULT_SEARCH_ENTITY_TYPE]: persistIncidentState((state, { payload }) => {
    return state.set('defaultSearchEntityType', payload);
  }),

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_STARTED]: (state, { payload: { entityId, entityType, timeFrameName } }) => {
    return state.merge({
      searchEntity: { id: entityId, type: entityType },
      searchTimeFrameName: timeFrameName,
      searchResults: [],
      searchStatus: 'streaming'
    });
  },

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED]: (state, { payload }) => {
    return state.set('stopSearchStream', payload);
  },

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH]: (state, { payload: { data, meta } }) => {
    const searchResults = state.searchResults || [];
    return state.merge({
      searchResults: [ ...searchResults, ...data ],
      searchStatus: meta.complete ? 'complete' : 'streaming'
    });
  },

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_COMPLETED]: (state) => {
    return state.set('stopSearchStream', null);
  },

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_STOPPED]: (state) => {
    return state.merge({
      stopSearchStream: null,
      searchStatus: 'stopped'
    });
  },

  [ACTION_TYPES.SEARCH_RELATED_INDICATORS_ERROR]: (state) => {
    return state.merge({
      searchStatus: 'error',
      stopSearchStream: null
    });
  },

  [ACTION_TYPES.ADD_RELATED_INDICATORS]: (state, action) => (
    handle(state, action, {
      start: (s) => s.set('addRelatedIndicatorsStatus', 'wait'),
      success: (s) => {
        const { payload: { response: { data: addedIndicatorIds, request: { data: { entity: { id } } } } } } = action;
        const { searchResults } = s;

        // Update any indicators in searchResults that match the indicators in payload
        const searchResultsUpdated = searchResults.map((indicator) => {
          if (addedIndicatorIds.includes(indicator.id)) {
            // For the indicator(s) added to the incident, ensure they have the updated properties
            const updatedIndicator = indicator.merge({
              partOfIncident: true,
              incidentId: id
            });
            return updatedIndicator;
          }
          return indicator;
        });

        return s.merge({
          searchResults: searchResultsUpdated,
          addRelatedIndicatorsStatus: 'success'
        });
      },
      failure: (s) => s.set('addRelatedIndicatorsStatus', 'error')
    })
  ),

  [ACTION_TYPES.CLEAR_ADD_RELATED_INDICATORS_STATUS]: (state) => {
    return state.set('addRelatedIndicatorsStatus', null);
  }
}, Immutable.from(initialState));

export default incident;