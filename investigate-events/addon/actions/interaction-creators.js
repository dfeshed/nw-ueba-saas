import * as ACTION_TYPES from './types';

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
  const { seconds } = timeRange;
  // "/ 1000 | 0" removes milliseconds.
  const endTime = +new Date() / 1000 | 0;
  // If user selects "All Data", seconds is zero.
  const startTime = seconds ? endTime - seconds : 0;
  return {
    type: ACTION_TYPES.SET_QUERY_TIME_RANGE,
    payload: { startTime, endTime }
  };
};

export const setSelectedEvent = (event, index) => {
  const metas = event ? event.metas : undefined;
  const sessionId = event ? event.sessionId : undefined;
  return {
    type: ACTION_TYPES.SET_SELECTED_EVENT,
    payload: {
      eventIndex: index,
      eventMetas: metas,
      sessionId
    }
  };
};

export const setSessionId = (serviceId) => ({
  type: ACTION_TYPES.SESSION_SELECTED,
  payload: serviceId
});

export const setServiceId = (serviceId) => ({
  type: ACTION_TYPES.SERVICE_SELECTED,
  payload: serviceId
});

export const setReconOpen = () => ({
  type: ACTION_TYPES.SET_RECON_VIEWABLE,
  payload: true
});

export const setReconClosed = () => ({
  type: ACTION_TYPES.SET_RECON_VIEWABLE,
  payload: false
});