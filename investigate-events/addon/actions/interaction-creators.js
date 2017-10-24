import * as ACTION_TYPES from './types';
import { getServiceSummary } from './data-creators';
import { getDbStartTime, getDbEndTime } from '../reducers/investigate/services/selectors';

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

export const setQueryParams = (params) => ({
  type: ACTION_TYPES.SET_QUERY_PARAMS,
  payload: params
});

export const setQueryString = (queryString) => ({
  type: ACTION_TYPES.SET_QUERY_STRING,
  payload: queryString
});

/**
 * Takes a time range object and calculates the start and end dates. The
 * timeRange object has the following properties:
 * { id: 'LAST_HOUR', name: 'Last 1 Hour', seconds: 360 }
 * @param {object} timeRange The time range
 * @public
 */
export const setQueryTimeRange = (timeRange) => {
  return (dispatch, getState) => {
    const state = getState();
    const dbEndTime = getDbEndTime(state);
    const dbStartTime = getDbStartTime(state);

    const wallClockTime = +new Date() / 1000 | 0; // "/ 1000 | 0" removes milliseconds.
    const { seconds } = timeRange;
    // TODO placeholder for the preference setting
    // Setting for the preferred time option (wallclock vs collection time)
    // Default option is the collection (db) time
    const preferencesFlag = false;
    const endTime = preferencesFlag ? wallClockTime : dbEndTime;
    const startTime = seconds ? endTime - seconds : dbStartTime;
    dispatch({
      type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
      payload: { startTime, endTime }
    });
  };
};

export const setSelectedEvent = (event) => {
  const metas = event ? event.metas : undefined;
  const sessionId = event ? event.sessionId : undefined;
  return {
    type: ACTION_TYPES.SET_SELECTED_EVENT,
    payload: {
      eventMetas: metas,
      sessionId
    }
  };
};

export const setSessionId = (serviceId) => ({
  type: ACTION_TYPES.SESSION_SELECTED,
  payload: serviceId
});

export const setServiceId = (serviceId) => {
  return (dispatch) => {
    dispatch({
      type: ACTION_TYPES.SERVICE_SELECTED,
      payload: serviceId
    });
    dispatch(getServiceSummary());
  };
};

export const setReconOpen = () => ({
  type: ACTION_TYPES.SET_RECON_VIEWABLE,
  payload: true
});

export const setReconClosed = () => ({
  type: ACTION_TYPES.SET_RECON_VIEWABLE,
  payload: false
});