import * as ACTION_TYPES from './types';
import moment from 'moment';
import { lookup } from 'ember-dependency-lookup';
import { later } from '@ember/runloop';

import { resultCountAtThreshold } from 'investigate-events/reducers/investigate/event-count/selectors';
import { fetchInvestigateData, getServiceSummary, updateSort } from './data-creators';
import { getDictionaries, queryIsRunning } from './initialization-creators';
import { cancelEventCountStream } from './event-count-creators';
import { cancelEventsStream } from './events-creators';
import { replaceAllGuidedPills, unstashPills } from './pill-creators';
import {
  getDbStartTime,
  getDbEndTime,
  hasMinimumCoreServicesVersionForColumnSorting
} from '../reducers/investigate/services/selectors';
import { useDatabaseTime, selectedTimeRange, isDirty } from '../reducers/investigate/query-node/selectors';
import {
  getCurrentPreferences,
  getDefaultPreferences,
  selectedColumnGroup
} from 'investigate-events/reducers/investigate/data-selectors';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import {
  SORT_ORDER,
  areAllEventsSelected,
  nestChildEvents
} from 'investigate-events/reducers/investigate/event-results/selectors';
import { isConsoleEmpty } from 'investigate-events/reducers/investigate/query-stats/selectors';
import { createParens } from 'investigate-events/util/query-parsing';
import { CLOSE_PAREN, OPEN_PAREN } from 'investigate-events/constants/pill';

/**
 * reset sort state to ensure the column being sorted on exists
 * time is the default sort meta, and has it's own default in preferences
 * @param {object} prefs  e.g. { currentReconView: "PACKET", isHeaderOpen: true, isMetaShown: true, ... }
 * @param {*} dispatch
 */
const _resetSortState = (dispatch, prefs) => {
  const sortDirection = (prefs && prefs.eventTimeSortOrder) || SORT_ORDER.ASC;
  const sortField = 'time';
  dispatch(updateSort('time', sortDirection));
  const params = updateUrl(window.location.search, {
    sortField,
    sortDir: sortDirection
  });
  history.pushState(
    null,
    document.querySelector('title').innerHTML,
    `${window.location.pathname}?${params}`
  );
};

/**
 * Determines if the pills need to be wrapped in parentheses. This is true for
 * most cases unless the pills are already wrapped.
 * @param {Object[]} pills
 * @return {boolean}
 * @private
 */
const _shouldBeWrappedInParens = (pills) => {
  const [firstPill] = pills;
  const lastPill = pills[pills.length - 1];
  const hasWrappingParens = (
    firstPill.type === OPEN_PAREN &&
    lastPill.type === CLOSE_PAREN &&
    firstPill.twinId === lastPill.twinId
  );
  return !hasWrappingParens;
};

/**
 *
 * @param {boolean} dispatchStatus
 * In some cases -- when the user is doing something other than cancelling the query,
 * we want to programmatically stop all querying without setting a cancel status.
 * For instance, when we want to restart a new query and before that want to make
 * sure the previous query is stopped, we pass in dispatchStatus = false.
 * @public
 */
export const cancelQuery = (dispatchStatus = true) => {
  return (dispatch) => {
    cancelEventCountStream();
    cancelEventsStream();
    dispatch(queryIsRunning(false));
    if (dispatchStatus) {
      dispatch({
        type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
        payload: 'canceled'
      });
    }
  };
};

export const setSearchScroll = (searchScrollIndex) => {
  return {
    type: ACTION_TYPES.SET_SEARCH_SCROLL,
    searchScrollIndex
  };
};

export const searchForTerm = (searchTerm, searchScrollIndex) => {
  return {
    type: ACTION_TYPES.SET_SEARCH_TERM,
    searchTerm,
    searchScrollIndex
  };
};

export const setMetaPanelSize = (size) => {
  if (size) {
    return {
      type: ACTION_TYPES.SET_META_PANEL_SIZE,
      payload: size
    };
  }
};

export const setReconPanelSize = (size) => {
  if (size) {
    return {
      type: ACTION_TYPES.SET_RECON_PANEL_SIZE,
      payload: size
    };
  }
};

/**
 * Takes a time range object and calculates the start and end dates. The
 * timeRange object has the following properties:
 * `{ id: 'LAST_HOUR', name: 'Last 1 Hour', value: 1, unit: 'hours' }`
 * Since we're rounding out to the full minute (start time of 0 seconds and end
 * time of 59 seconds), the final `startTime` has 1 minute added to it so that
 * it most closely represents the desired time range.
 * @param {object} timeRange The time range
 * @public
 */
export const setQueryTimeRange = ({ id, value, unit }) => {
  // There is a scheduler that polls for summary data every minute that will update the state and
  // re-calculate the start and end times if there is a change from the previous summaryData.
  // However, if the timeRanges id is custom we don't want to update the start/end time as this
  // specific start/end time is manually picked by the user.
  if (id !== TIME_RANGES.CUSTOM_TIME_RANGE_ID) {
    return (dispatch, getState) => {
      const state = getState();
      // Get the database start/end times. If they are 0, then use browser time
      // For startTime, set time to 1970 if DB time was 0.
      const dbEndTime = getDbEndTime(state) || moment().unix();
      const dbStartTime = getDbStartTime(state) || moment(0).unix();
      let endTime, startTime;
      if (useDatabaseTime(state)) {
        endTime = moment(dbEndTime * 1000).endOf('minute');
      } else {
        endTime = moment().endOf('minute');
      }

      if (value) {
        startTime = moment(endTime).subtract(value, unit).add(1, 'minutes').startOf('minute');
        // if the precision is in months - for last 30 days, momentjs takes last 30 days 23 hrs 59 mins 59 secs.
        // So adding a day to startTime to negate the effect.
        if (unit == 'months') {
          startTime = startTime.add(1, 'day');
        }
      } else {
        startTime = moment(dbStartTime * 1000).startOf('minute');
      }

      dispatch({
        type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
        payload: {
          startTime: startTime.unix(),
          endTime: endTime.unix(),
          selectedTimeRangeId: id
        }
      });
    };
  }
};

/**
 * Takes a custom start and end times in ms. This will only be called when the user changes the start/end time manually on the individual time/date input units in the timeRange component.
 * @param {number} start
 * @param {number} end
 * @public
 */
export const setCustomTimeRange = (start, end) => {
  return {
    type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
    payload: {
      startTime: start / 1000,
      endTime: end / 1000,
      selectedTimeRangeId: TIME_RANGES.CUSTOM_TIME_RANGE_ID
    }
  };
};

/**
 * setTimeRangeError will be called when there is an error in the time-range component.
 * Errors can be date errors (out of bounds), range errors (start time greater than end time) etc.
 * Error state can only be achieved when the user manually sets an incorrect date/time unit.
 * So dispatch CUSTOM ID when we detect an error.
 * Also dispatch the invalid time selections to justify the error notification,
 * else the invalid time selection reverts to previous valid time selection, leaving the user confused
 * why the red border persists with the correct time.
 * @public
 */
export const setTimeRangeError = (error, start, end) => {
  return {
    type: ACTION_TYPES.SET_TIME_RANGE_ERROR,
    payload: {
      startTime: start / 1000,
      endTime: end / 1000,
      selectedTimeRangeId: TIME_RANGES.CUSTOM_TIME_RANGE_ID
    }
  };
};

export const setService = (service) => {
  return (dispatch, getState) => {
    const { serviceId } = getState().investigate.queryNode;
    if (serviceId !== service.id) {
      dispatch({
        type: ACTION_TYPES.SERVICE_SELECTED,
        payload: service.id
      });
      dispatch(getDictionaries());
      dispatch(getServiceSummary());
    }
  };
};

export const setReconOpen = (event = {}) => {
  const { sessionId } = event;
  return {
    type: ACTION_TYPES.SET_RECON_VIEWABLE,
    payload: {
      eventData: { sessionId },
      isReconOpen: true
    }
  };
};

export const setReconClosed = () => {
  return (dispatch, getState) => {
    const { isReconOpen } = getState().investigate.data;
    if (isReconOpen) {
      dispatch({
        type: ACTION_TYPES.SET_RECON_VIEWABLE,
        payload: {
          eventData: { sessionId: undefined },
          isReconOpen: false
        }
      });
    }
  };
};

export const setColumnGroup = (selectedGroup) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.SET_SELECTED_COLUMN_GROUP,
      payload: selectedGroup.id
    });
    const state = getState();
    // Extracts (and merges) all the preferences from redux state and sends to the backend for persisting.
    const prefService = lookup('service:preferences');
    prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(state), getDefaultPreferences(state));
    const prefs = state.investigate.data.eventAnalysisPreferences;

    if (hasMinimumCoreServicesVersionForColumnSorting(getState())) {
      // reset sort state to ensure the column being sorted on exists
      // time is the default sort meta, and has it's own default in preferences
      _resetSortState(dispatch, prefs);
    }

    // column group can also be set as a part of `reset to 'SUMMARY'` when the user's
    // selected column group is no longer available in database.
    // we would not want setColumnGroup to execute query, etc unless the events table
    // was alreay rendered when the column group was set
    const { status } = getState().investigate.eventResults;
    if (status) {
      dispatch(cancelQuery(false));
      dispatch(setReconClosed());
      dispatch(isQueryExecutedByColumnGroup(true));
      dispatch(fetchInvestigateData());
    }
  };
};

/**
 * select a profile and use its column group and prequery conditions
 * @param {object} profile profile to select
 * @param {function} executeQuery function to execute query
 */
export const setProfile = (profile, executeQuery) => {
  return (dispatch, getState) => {
    const currentState = getState();
    const prequeryPills = profile.preQueryPillsData?.asMutable() || [];
    const currentColumnGroupId = selectedColumnGroup(currentState);
    const newColumnGroupId = profile.columnGroup?.id;

    // Before setting the profile which may contain its own pills
    // we want the primary query-pills container to resume it's
    // normal activities
    dispatch(unstashPills());

    // if there are any prequery pills, wrap them in parens if needed
    if (prequeryPills.length > 1 && _shouldBeWrappedInParens(prequeryPills)) {
      const [ open, close ] = createParens();
      prequeryPills.unshift(open);
      prequeryPills.push(close);
    }
    // replace any existing pills with profile's prequery pills
    // whether column group changed or not
    dispatch(replaceAllGuidedPills(prequeryPills));

    // if new profile's column group is different than currently selected column group
    if (newColumnGroupId && newColumnGroupId !== currentColumnGroupId) {
      const newState = getState();

      // apply the new column group
      dispatch({
        type: ACTION_TYPES.SET_SELECTED_COLUMN_GROUP,
        payload: newColumnGroupId
      });
      // Extracts (and merges) all the preferences from redux state and sends to the backend for persisting.
      const prefService = lookup('service:preferences');
      prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(newState), getDefaultPreferences(newState));
      const prefs = newState.investigate.data.eventAnalysisPreferences;

      // reset sort state
      if (hasMinimumCoreServicesVersionForColumnSorting(getState())) {
        _resetSortState(dispatch, prefs);
      }

      dispatch(cancelQuery(false));
      dispatch(setReconClosed());

      // if query changed, execute
      // executeQuery takes place only if column group and query both changed
      if (isDirty(newState)) {
        executeQuery();
      } else {
        // if query did not change, fetch data
        dispatch(isQueryExecutedByColumnGroup(true));
        dispatch(fetchInvestigateData());
      }
    }
  };
};

// Approach discouraged : If there is a need to alter URL, use executeQuery route action as opposed to
// updating URL manually through this function.
export const updateUrl = (initialUrl, updateParameters) => {
  const params = new URLSearchParams(initialUrl);

  for (const param in updateParameters) {
    params.set(param, updateParameters[param]);
  }

  return params.toString();
};

// update timeRange if not custom, and fetch updated event data
export const setQueryTimeFormat = () => {
  return (dispatch, getState) => {

    const range = selectedTimeRange(getState());

    dispatch(setQueryTimeRange(range));

    /* changing queryTimeFormat preference should executeQuery via route, inorder to take
     * the time stamp changes into account while fetching query results
     * fetchInvestigateData on the other hand is intervened by getActiveQueryNode which
     * rejects changes (like startTime and endTime) that cause queryHash to change,
     * resulting in previousQueryParams to fetch results.
     */
    const router = lookup('service:-routing').get('router');
    router.send('executeQuery');

  };
};

// update sort state, perform ux cleanup, and fetch updated event data
export const setSort = (sortField, sortDirection, isQueryExecutedBySort) => {
  return (dispatch, getState) => {
    // deselect events on sort, otherwise could break download order
    dispatch({ type: ACTION_TYPES.SELECT_EVENTS, payload: [] });
    dispatch(cancelQuery(false));
    dispatch(setReconClosed());

    if (sortField && sortDirection) {
      const state = getState();
      if (hasMinimumCoreServicesVersionForColumnSorting(state) && resultCountAtThreshold(state)) {
        // there are more events that cannot be sorted in the client
        // query executed by route update
        dispatch(updateSort(sortField, sortDirection, isQueryExecutedBySort));
      } else {
        // we have everything and can sort in the client
        // or sorting is not supported in core because of service version
        dispatch({ type: ACTION_TYPES.SORT_IN_CLIENT_BEGIN });
        dispatch(updateSort(sortField, sortDirection, isQueryExecutedBySort));
        // data is eventually sorted via selector which doesn't offer us a callback or promise to resolve
        // add static timer to facilitate toggling between sorting and complete states
        // if dispatched before sort is complete, it will be held up while the browser is locked up sorting

        later(() => {
          dispatch({
            type: ACTION_TYPES.SORT_IN_CLIENT_COMPLETE,
            payload: {
              sortField,
              sortDir: sortDirection
            }
          });
        }, 750);
      }

      try {
        const { router } = lookup('service:-routing');
        router.transitionTo({
          queryParams: {
            eid: undefined,
            sortField,
            sortDir: sortDirection
          }
        });
      } catch {
        return;
      }
    }
  };
};

export const isQueryExecutedByColumnGroup = (flag) => ({
  type: ACTION_TYPES.SET_QUERY_EXECUTED_BY_COLUMN_GROUP_FLAG,
  payload: flag
});

/**
 * Toggles visibility of the query console
 * @public
 */
export const toggleQueryConsole = () => {
  return (dispatch, getState) => {
    if (!isConsoleEmpty(getState())) {
      dispatch({ type: ACTION_TYPES.TOGGLE_QUERY_CONSOLE });
    }
  };
};

// selectAll would need items from events table as opposed to data from state as they could be sorted in client
export const toggleSelectAllEvents = () => {
  return (dispatch, getState) => {

    const newIds = {};
    if (!areAllEventsSelected(getState())) {
      const items = nestChildEvents(getState());
      for (let i = 0; i < items.length; i++) {
        newIds[i] = items[i].sessionId;
      }
    }

    dispatch({
      type: ACTION_TYPES.SELECT_EVENTS,
      payload: newIds
    });
  };
};

export const toggleEventSelection = ({ sessionId }, index) => {
  return (dispatch, getState) => {
    const state = getState().investigate.eventResults;
    const { selectedEventIds } = state;
    if (selectedEventIds[index]) {
      // if the event is already selected, deselect it
      dispatch({ type: ACTION_TYPES.DESELECT_EVENT, payload: index });
    } else {
      // if the toggled event is not already selected, select the event
      const newIds = {
        ...selectedEventIds
      };
      newIds[index] = sessionId;
      dispatch({ type: ACTION_TYPES.SELECT_EVENTS, payload: newIds });
    }
  };
};
