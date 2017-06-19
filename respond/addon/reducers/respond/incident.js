import * as ACTION_TYPES from 'respond/actions/types';
import reduxActions from 'redux-actions';
import { handle } from 'redux-pack';
import { load, persist } from './util/local-storage';
import fixNormalizedEvents from './util/events';
import { toggle } from 'respond/utils/immut/array';

const localStorageKey = 'rsa::nw::respond::incident';

let initialState = {
  // id of the incident that owns `info` & `storyline`
  id: null,

  // incident details
  info: null,

  // either 'wait', 'error' or 'completed'
  infoStatus: null,

  // incident storyline information
  storyline: null,

  // either 'streaming', 'error' or 'completed'
  storylineStatus: null,

  // function to stop the current `storyline` stream request, if any
  stopStorylineStream: null,

  // events for the alerts currently in `storyline`
  storylineEvents: null,

  // status of the current request for storyline events, if any; either 'streaming', 'paused', 'complete' or 'error'
  storylineEventsStatus: null,

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

  tasksStatus: null,

  isShowingTasksAndJournal: false,

  tasksJournalMode: 'journal'
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
    const { viewMode, inspectorWidth, hideViz, tasksJournalMode, isShowingTasksAndJournal } = state;
    persist({ viewMode, inspectorWidth, hideViz, tasksJournalMode, isShowingTasksAndJournal }, localStorageKey);
    return state;
  });
};

// Updates the state value with the value updated on the server.
// If the updated incidents includes the incident currently in the incident details route,
// we must update that incident's info as well.
const _handleUpdates = (action) => {
  return (state) => {
    const { payload: { request: { updates, entityIds } } } = action;
    const { id, info } = state;
    const updatedIncidentInfo = entityIds.includes(id) ? { ...info, ...updates } : info;
    return {
      ...state,
      info: updatedIncidentInfo
    };
  };
};

const incident = reduxActions.handleActions({

  [ACTION_TYPES.INITIALIZE_INCIDENT]: (state, { payload }) => ({
    // reset state for a new incident id, even if it matches the old incident id,
    // because we don't want to reuse info, we want to reload it in case it may have changed on server
    ...initialState,
    id: payload,

    // there are some visual properties (not server data) properties which should be preserved
    // they should not be reset to initialState for every incident
    inspectorWidth: state.inspectorWidth || initialState.inspectorWidth,
    viewMode: state.viewMode || initialState.viewMode,
    isShowingTasksAndJournal: state.isShowingTasksAndJournal || initialState.isShowingTasksAndJournal,
    tasksJournalMode: state.tasksJournalMode || initialState.tasksJournalMode,
    hideViz: state.hideViz || initialState.hideViz
  }),

  [ACTION_TYPES.FETCH_INCIDENT_DETAILS]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, info: null, infoStatus: 'wait' }),
      failure: (s) => ({ ...s, infoStatus: 'error' }),
      success: (s) => ({ ...s, info: action.payload.data, infoStatus: 'completed' })
    });
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED]: (state) => ({
    ...state,
    storyline: [],
    storylineStatus: 'streaming'
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STREAM_INITIALIZED]: (state, { payload }) => ({
    ...state,
    stopStorylineStream: payload
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH]: (state, { payload: { data, meta } }) => {
    // Tag each retrieved indicator with its parent incident id.
    // This is useful downstream for mapping indicators back to their parent.
    const storylineId = state.id;
    data = data || [];
    data.forEach((indicator) => {
      indicator.storylineId = storylineId;
    });

    state.storyline = state.storyline || [];
    return {
      ...state,
      storyline: [ ...state.storyline, ...data ],
      storylineStatus: meta.complete ? 'completed' : 'streaming'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_COMPLETED]: (state) => ({
    ...state,
    stopStorylineStream: null
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR]: (state) => ({
    ...state,
    storylineStatus: 'error',
    stopStorylineStream: null
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_STREAM_INITIALIZED]: (state) => ({
    ...state,
    storylineEvents: []
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_REQUEST_BATCH]: (state) => ({
    ...state,
    storylineEventsStatus: 'streaming'
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_RETRIEVE_BATCH]: (state, { payload: { indicatorId, events } }) => {
    events = events || [];
    events.forEach((evt, index) => {
      // Tag each retrieved event with its parent indicator id.
      // This is useful downstream for mapping events back to their parent.
      evt.indicatorId = indicatorId;

      // Ensure each event has an id.
      // This is useful for selecting individual events in the UI.
      if (!evt.id) {
        evt.id = `${indicatorId}:${index}`;
      }
    });

    // Check for data capture & normalization errors and correct them.
    fixNormalizedEvents(events);

    state.storylineEvents = state.storylineEvents || [];
    return {
      ...state,
      storylineEvents: [ ...state.storylineEvents, { indicatorId, events } ],
      storylineEventsStatus: 'paused'
    };
  },

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_COMPLETED]: (state) => ({
    ...state,
    storylineEventsStatus: 'complete'
  }),

  [ACTION_TYPES.FETCH_INCIDENT_STORYLINE_EVENTS_ERROR]: (state) => ({
    ...state,
    storylineEventsStatus: 'error'
  }),

  [ACTION_TYPES.SET_VIEW_MODE]: persistIncidentState((state, { payload }) => ({
    ...state,
    viewMode: payload
  })),

  [ACTION_TYPES.RESIZE_INCIDENT_INSPECTOR]: persistIncidentState((state, { payload }) => ({
    ...state,
    inspectorWidth: payload
  })),

  [ACTION_TYPES.SET_HIDE_VIZ]: persistIncidentState((state, { payload }) => ({
    ...state,
    hideViz: payload
  })),

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
    return {
      ...state,
      selection: newSelection
    };
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
    return {
      ...state,
      selection: newSelection
    };
  },

  [ACTION_TYPES.UPDATE_INCIDENT]: (state, action) => {
    return handle(state, action, {
      success: _handleUpdates(action)
    });
  },

  [ACTION_TYPES.FETCH_REMEDIATION_TASKS_FOR_INCIDENT]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s, tasks: [], tasksStatus: 'wait' }),
      failure: (s) => ({ ...s, tasksStatus: 'error' }),
      success: (s) => ({ ...s, tasks: action.payload.data, tasksStatus: 'completed' })
    });
  },

  [ACTION_TYPES.CREATE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => ({ ...s, tasks: [action.payload.data, ...s.tasks] })
    });
  },

  [ACTION_TYPES.DELETE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => {
        const removedItemIds = action.payload.data;
        // Filter out newly deleted items from the main items array
        const updatedItems = state.tasks.filter((item) => (!removedItemIds.includes(item.id)));
        return {
          ...s,
          tasks: updatedItems
        };
      }
    });
  },

  [ACTION_TYPES.UPDATE_REMEDIATION_TASK]: (state, action) => {
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => {
        const { payload: { request: { updates, entityIds } } } = action;
        const updatedEntities = state.tasks.map((entity) => {
          return entityIds.includes(entity.id) ? { ...entity, ...updates } : entity;
        });
        return {
          ...s,
          tasks: updatedEntities
        };
      }
    });
  },

  [ACTION_TYPES.SET_TASKS_JOURNAL_MODE]: persistIncidentState((state, { payload }) => ({
    ...state,
    tasksJournalMode: payload || 'journal'
  })),

  [ACTION_TYPES.TOGGLE_TASKS_JOURNAL]: persistIncidentState((state) => ({
    ...state,
    isShowingTasksAndJournal: !state.isShowingTasksAndJournal
  })),

  [ACTION_TYPES.CREATE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => {
        const { payload: { data } } = action;
        return {
          ...s,
          info: {
            ...s.info,
            notes: [...notes, data]
          }
        };
      }
    });
  },

  [ACTION_TYPES.DELETE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => {
        const { payload: { request: { journalId } } } = action;
        return {
          ...s,
          info: {
            ...s.info,
            notes: notes.filter((note) => (note.id !== journalId))
          }
        };
      }
    });
  },

  [ACTION_TYPES.UPDATE_JOURNAL_ENTRY]: (state, action) => {
    const notes = state.info.notes || [];
    return handle(state, action, {
      start: (s) => ({ ...s }),
      failure: (s) => ({ ...s }),
      success: (s) => {
        const { payload: { request: { journalId, journalMap } } } = action;
        return {
          ...s,
          info: {
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
          }
        };
      }
    });
  }

}, initialState);

export default incident;