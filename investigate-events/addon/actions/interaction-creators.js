import * as ACTION_TYPES from './types';
import moment from 'moment';
import { getServiceSummary } from './data-creators';
import { getDictionaries } from './initialization-creators';
import { getDbStartTime, getDbEndTime } from '../reducers/investigate/services/selectors';
import { useDatabaseTime } from '../reducers/investigate/query-node/selectors';
import { getCurrentPreferences, getDefaultPreferences } from 'investigate-events/reducers/investigate/data-selectors';
import { lookup } from 'ember-dependency-lookup';


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

export const setQueryFilterMeta = (params) => ({
  type: ACTION_TYPES.SET_QUERY_FILTER_META,
  payload: params
});

export const setQueryParamsForTests = (params) => ({
  type: ACTION_TYPES.SET_QUERY_PARAMS_FOR_TESTS,
  payload: params
});

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
      dispatch(setQueryFilterMeta([]));
      dispatch(getDictionaries());
      dispatch(getServiceSummary());
    }
  };
};

export const setReconOpen = (event = {}) => {
  const { meta: eventMetas, sessionId } = event;
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