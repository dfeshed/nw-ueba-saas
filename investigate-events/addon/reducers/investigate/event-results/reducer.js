import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import { handle } from 'redux-pack';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { SORT_ORDER } from './selectors';

export const MAX_EVENTS_ALLOWED = 10000;

const _initialState = Immutable.from({
  // streaming, complete, stopped, between-streams
  status: undefined,

  data: null,
  cachedData: null,
  reason: undefined,
  streamLimit: MAX_EVENTS_ALLOWED, // default default. In case our event-settings api returns an error
  streamBatch: 1000,
  message: undefined,
  selectedEventIds: {},
  // Pref might change in the middle of a query. Keeping a copy of preference with which the last query was performed.
  eventTimeSortOrderPreferenceWhenQueried: undefined,
  searchTerm: null,
  searchScrollIndex: -1,
  visibleColumns: []
});

// * `data` is an array of objects with the following properties
// {
//   sessionId,
//   time,
//   metas,
//   ...metas converted to props,
//   log, // if log
//   logStatus
// }

export default handleActions({
  [ACTION_TYPES.SORT_IN_CLIENT_COMPLETE]: (state) => {
    const newState = state.merge({
      data: state.cachedData,
      status: 'complete',
      cachedData: null
    });
    return newState;
  },

  [ACTION_TYPES.SORT_IN_CLIENT_BEGIN]: (state) => {
    return state.merge({
      status: 'sorting',
      data: [],
      cachedData: state.data
    });
  },

  [ACTION_TYPES.SET_SEARCH_TERM]: (state, { searchTerm, searchScrollIndex }) => {
    return state.merge({
      searchTerm,
      searchScrollIndex
    });
  },

  [ACTION_TYPES.SET_SEARCH_SCROLL]: (state, { searchScrollIndex }) => {
    return state.set('searchScrollIndex', searchScrollIndex);
  },

  [ACTION_TYPES.SET_VISIBLE_COLUMNS]: (state, { payload }) => {
    return state.set('visibleColumns', payload);
  },

  [ACTION_TYPES.SELECT_EVENTS]: (state, { payload }) => {
    return state.set('selectedEventIds', payload);
  },

  [ACTION_TYPES.DESELECT_EVENT]: (state, { payload }) => {
    const newIds = {
      ...state.selectedEventIds
    };
    delete newIds[payload];
    return state.set('selectedEventIds', newIds);
  },

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state, { payload: { eventTimeSortOrderPreferenceWhenQueried } }) => {
    return state.merge({
      data: [],
      message: undefined,
      reason: undefined,
      status: 'streaming',
      selectedEventIds: {},
      eventTimeSortOrderPreferenceWhenQueried
    });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state, { payload }) => {
    // Merge the data into the current state data
    const newEvents = state.data.asMutable().concat(payload);
    // truncate the array if larger than max
    if (newEvents.length > state.streamLimit) {
      if (window.DEBUG_STREAMS) {
        // eslint-disable-next-line no-console
        console.log(`Truncating ${newEvents.length} results down to ${state.streamLimit}`);
      }

      // If we have newest data and it is sorted oldest first
      // then we want to truncate the first items not the last
      // Example 110k records, oldest records first, the newest
      // 100k items are items 10k-110k
      // If it is oldest data, then we are not truncating as we
      // get the events we want.
      if (state.eventTimeSortOrderPreferenceWhenQueried === SORT_ORDER.ASC) {
        // remove from the beginning
        newEvents.splice(0, newEvents.length - state.streamLimit);
      } else {
        // just truncate the end
        newEvents.length = state.streamLimit;
      }

    }

    const newState = state.set('data', Immutable.from(newEvents));

    return newState;
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_STATUS]: (state, { payload }) => {
    return state.set('status', payload);
  },

  [ACTION_TYPES.SET_EVENTS_PAGE_ERROR]: (state, { payload }) => {
    return state.merge(payload);
  },

  [ACTION_TYPES.SET_LOG]: (state, { payload }) => {
    payload.forEach((data) => {
      const { log, sessionId } = data || {};
      const item = _find(state.data, sessionId);
      if (item) {
        let updatedItem;
        if (data.errorCode) {
          // codes other than 0 are errors
          updatedItem = item.merge({
            logStatus: 'rejected',
            errorCode: data.errorCode
          });
        } else {
          // set new log and status
          updatedItem = item.merge({
            log: (item.log || '') + log,
            logStatus: 'resolved'
          });
        }
        // replace event in array
        const newData = _update(state.data, sessionId, updatedItem);
        state = state.set('data', newData);
      }
    });

    return state;
  },

  [ACTION_TYPES.SET_LOG_STATUS]: (state, { sessionId, status }) => {
    // Update event's logStatus property
    const item = _find(state.data, sessionId);
    const updatedItem = item.set('logStatus', status);
    // replace event in array
    const newData = _update(state.data, sessionId, updatedItem);
    return state.set('data', newData);
  },

  [ACTION_TYPES.SET_MAX_EVENT_LIMIT]: (state, action) => {
    return handle(state, action, {
      failure: (s) => s, // in creators by generic handlers
      success: (s) => {
        const { calculatedEventLimit } = action.payload.data;
        return s.set('streamLimit', calculatedEventLimit);
      }
    });
  }
}, _initialState);

const _find = (data, sessionId) => data.find((d) => d.sessionId === sessionId);

const _update = (data, sessionId, updatedItem) => data.map((d) => {
  return (d.sessionId === sessionId) ? updatedItem : d;
});
