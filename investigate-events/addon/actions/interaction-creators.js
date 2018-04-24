import * as ACTION_TYPES from './types';
import moment from 'moment';
import { getServiceSummary } from './data-creators';
import { getDictionaries } from './initialization-creators';
import { getDbStartTime, getDbEndTime } from '../reducers/investigate/services/selectors';
import { useDatabaseTime } from '../reducers/investigate/query-node/selectors';
import {
  getCurrentPreferences,
  getDefaultPreferences
} from 'investigate-events/reducers/investigate/data-selectors';
import { lookup } from 'ember-dependency-lookup';
import { encodeMetaFilterConditions } from './fetch/utils';

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
      // if the precision is in months - for last 30 days, momentjs takes last 30 days 23 hrs.
      // So adding 23 hrs to startTime to negate the effect.
      if (unit == 'months') {
        startTime = startTime.add(23, 'hours');
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
  const { metas: eventMetas, sessionId } = event;
  return {
    type: ACTION_TYPES.SET_RECON_VIEWABLE,
    payload: {
      eventData: { eventMetas, sessionId },
      isReconOpen: true
    }
  };
};

export const setReconClosed = () => ({
  type: ACTION_TYPES.SET_RECON_VIEWABLE,
  payload: {
    eventData: { eventMetas: undefined, sessionId: undefined },
    isReconOpen: false
  }
});

export const setQueryView = (view, filters = []) => {
  return (dispatch) => {
    if (filters && filters.length > 0) {
      let rawText = encodeMetaFilterConditions(filters);
      rawText = rawText.replace(/(&&\s)$.*/g, '').trim(); // remove && from the end
      dispatch(setFreeFormText(rawText));
    }
    dispatch(toggleFocusFlag(true));
    dispatch({
      type: ACTION_TYPES.SET_QUERY_VIEW,
      payload: view
    });
  };
};

export const setFreeFormText = (text) => {
  return (dispatch, getState) => {
    const previousText = getState().investigate.queryNode.freeFormText;
    if (previousText !== text) {
      dispatch({
        type: ACTION_TYPES.SET_FREE_FORM_TEXT,
        payload: text
      });
    }
  };
};

export const toggleFocusFlag = (flag) => {
  return (dispatch, getState) => {
    const previousFlag = getState().investigate.queryNode.toggledOnceFlag;
    if (previousFlag !== flag) {
      dispatch({
        type: ACTION_TYPES.TOGGLE_FOCUS_FLAG,
        payload: flag
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
    // Extracts (and merges) all the preferences from redux state and sends to the backend for persisting.
    const prefService = lookup('service:preferences');
    prefService.setPreferences('investigate-events-preferences', null, getCurrentPreferences(getState()), getDefaultPreferences(getState()));
  };
};