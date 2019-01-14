import * as ACTION_TYPES from './types';
import moment from 'moment';
import { lookup } from 'ember-dependency-lookup';
import _ from 'lodash';

import { getServiceSummary } from './data-creators';
import { getDictionaries, queryIsRunning } from './initialization-creators';
import { cancelEventCountStream } from './event-count-creators';
import { cancelEventsStream } from './events-creators';
import { getDbStartTime, getDbEndTime } from '../reducers/investigate/services/selectors';
import { useDatabaseTime } from '../reducers/investigate/query-node/selectors';
import {
  getCurrentPreferences,
  getDefaultPreferences
} from 'investigate-events/reducers/investigate/data-selectors';
import TIME_RANGES from 'investigate-shared/constants/time-ranges';
import { isConsoleEmpty } from 'investigate-events/reducers/investigate/query-stats/selectors';

export const cancelQuery = () => {
  return (dispatch) => {
    cancelEventCountStream();
    cancelEventsStream();
    dispatch(queryIsRunning(false));
    dispatch({
      type: ACTION_TYPES.SET_EVENTS_PAGE_STATUS,
      payload: 'canceled'
    });
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
 * @public
 */
export const setTimeRangeError = () => {
  return {
    type: ACTION_TYPES.SET_TIME_RANGE_ERROR,
    payload: {
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

export const setQueryView = (queryView) => ({
  type: ACTION_TYPES.SET_QUERY_VIEW,
  payload: {
    queryView
  }
});

export const setColumnGroup = (selectedGroup) => {
  return (dispatch, getState) => {
    dispatch({
      type: ACTION_TYPES.SET_SELECTED_COLUMN_GROUP,
      payload: selectedGroup.id
    });
    // Extracts (and merges) all the preferences from redux state and sends to the backend for persisting.
    const prefService = lookup('service:preferences');
    prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(getState()), getDefaultPreferences(getState()));
  };
};

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

export const toggleSelectAllEvents = () => ({
  type: ACTION_TYPES.TOGGLE_SELECT_ALL_EVENTS
});

export const toggleEventSelection = ({ sessionId }) => {
  return (dispatch, getState) => {
    const state = getState().investigate.eventResults;
    const { allEventsSelected, selectedEventIds, data } = state;

    if (allEventsSelected) {
      // if all events already selected and one event is toggled
      // toggle allEventsSelected and also select all event ids minus the one just toggled
      dispatch(toggleSelectAllEvents());
      dispatch({
        type: ACTION_TYPES.SELECT_EVENTS,
        payload: _.without(data.map((d) => d.sessionId), sessionId)
      });
    } else {
      if (selectedEventIds.includes(sessionId)) {
        // otherwise, if the event is already selected, deselect it
        dispatch({ type: ACTION_TYPES.DESELECT_EVENT, payload: sessionId });
      } else {
        if (selectedEventIds.length === (getState().investigate.eventCount.data - 1)) {
          // if the event is not already selected, but it's the last unselected event
          // toggle allEventsSelected
          dispatch(toggleSelectAllEvents());
        } else {
          // lastly, if the toggled event is not already selected, and is not the last unselected event
          // select the event
          dispatch({ type: ACTION_TYPES.SELECT_EVENTS, payload: [sessionId] });
        }
      }
    }
  };
};