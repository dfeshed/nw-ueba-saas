import Immutable from 'seamless-immutable';
import { handleActions } from 'redux-actions';
import _ from 'lodash';
import sort from 'fast-sort';

import * as ACTION_TYPES from 'investigate-events/actions/types';
import { SORT_ORDER } from './selectors';

export const MAX_EVENTS_ALLOWED = window.MAX_EVENTS_ALLOWED || 50000;

const _initialState = Immutable.from({
  // streaming, complete, stopped, between-streams
  status: undefined,

  data: null,
  reason: undefined,
  streamLimit: MAX_EVENTS_ALLOWED,
  streamBatch: 1000,
  message: undefined,
  allEventsSelected: false,
  selectedEventIds: [],
  eventTimeSortOrder: 'Ascending',
  // Pref might change in the middle of a query. Keeping a copy of preference with which the last query was performed.
  eventTimeSortOrderPreferenceWhenQueried: undefined
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

  [ACTION_TYPES.INITIALIZE_INVESTIGATE]: (state) => {
    return state.merge({
      allEventsSelected: false,
      selectedEventIds: []
    });
  },

  [ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS]: (state) => {
    return state.merge({
      allEventsSelected: !state.allEventsSelected,
      selectedEventIds: []
    });
  },

  [ACTION_TYPES.SELECT_EVENTS]: (state, { payload }) => {
    return state.set('selectedEventIds', state.selectedEventIds.concat(payload));
  },

  [ACTION_TYPES.DESELECT_EVENT]: (state, { payload }) => {
    return state.set('selectedEventIds', _.without(state.selectedEventIds, payload));
  },

  [ACTION_TYPES.INIT_EVENTS_STREAMING]: (state, { payload: { eventTimeSortOrderPreferenceWhenQueried } }) => {
    return state.merge({
      data: [],
      message: undefined,
      reason: undefined,
      status: 'streaming',
      eventTimeSortOrderPreferenceWhenQueried
    });
  },

  [ACTION_TYPES.SET_EVENTS_PAGE]: (state, { payload }) => {
    // Merge the data into the current state data and perform a sort
    // Have to sort it all as data can come in out of order.
    let sortKey = 'desc';
    if (state.eventTimeSortOrderPreferenceWhenQueried === SORT_ORDER.ASC) {
      sortKey = 'asc';
    }
    let newEvents = state.data.asMutable().concat(payload);
    newEvents = sort(newEvents).by([
      { [sortKey]: 'timeAsNumber' },
      { [sortKey]: 'sessionId' }
    ]);

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
  }
  // NewestFirst code commented out
  /* [ACTION_TYPES.SET_PREFERENCES]: (state, { payload: { eventAnalysisPreferences } }) => {
    const eventTimeSortOrder = _.get(eventAnalysisPreferences, 'eventTimeSortOrder', state);
    return state.set('eventTimeSortOrder', eventTimeSortOrder);
  } */
}, _initialState);

const _find = (data, sessionId) => data.find((d) => d.sessionId === sessionId);

const _update = (data, sessionId, updatedItem) => data.map((d) => {
  return (d.sessionId === sessionId) ? updatedItem : d;
});
