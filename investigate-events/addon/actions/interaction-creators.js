import * as ACTION_TYPES from './types';
import { getServiceSummary } from './data-creators';
import { getDbStartTime, getDbEndTime } from '../reducers/investigate/services/selectors';
import moment from 'moment';

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
 * { id: 'LAST_HOUR', name: 'Last 1 Hour', value: 1, unit: 'hours' }
 * @param {object} timeRange The time range
 * @public
 */
export const setQueryTimeRange = ({ value, unit }) => {
  return (dispatch, getState) => {
    const state = getState();
    // TODO placeholder for the preference setting
    // Setting for the preferred time option (browser time vs collection time)
    // Default option is the collection (db) time
    const useBrowserTime = false;
    let endTime, startTime;

    if (useBrowserTime) {
      endTime = moment().endOf('minute');
    } else {
      endTime = moment(getDbEndTime(state) * 1000).endOf('minute');
    }

    if (value) {
      startTime = moment(endTime).subtract(value, unit).startOf('minute');
    } else {
      startTime = moment(getDbStartTime(state) * 1000).startOf('minute');
    }

    dispatch({
      type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
      payload: {
        startTime: startTime.unix(),
        endTime: endTime.unix()
      }
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