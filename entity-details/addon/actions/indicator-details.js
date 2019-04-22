import { INITIATE_INDICATOR, GET_INDICATOR_EVENTS, GET_INDICATOR_HISTORICAL_DATA, RESET_INDICATOR, INDICATOR_EVENTS_ERROR, INDICATOR_GRAPH_ERROR } from './types';
import { fetchData } from './fetch/data';
import { eventFilter, indicatorMapSettings } from 'entity-details/reducers/indicators/selectors';

export const resetIndicators = () => ({ type: RESET_INDICATOR });

export const getEvents = (indicatorId) => {
  return (dispatch, getState) => {
    const fetchObj = {
      restEndpointLocation: 'indicatorEvents',
      data: eventFilter(getState()),
      method: 'GET',
      urlParameters: indicatorId
    };
    fetchData(fetchObj).then((result) => {
      if (result === 'error') {
        dispatch({
          type: INDICATOR_EVENTS_ERROR
        });
      } else {
        const { data, total } = result;
        dispatch({
          type: GET_INDICATOR_EVENTS,
          payload: { data, total }
        });
        dispatch(getHistoricalData(indicatorId));
      }
    });
  };
};

export const getHistoricalData = (indicatorId) => {
  return (dispatch, getState) => {
    const fetchObj = {
      restEndpointLocation: 'historicalData',
      data: indicatorMapSettings(getState()).params,
      method: 'GET',
      urlParameters: indicatorId
    };
    fetchData(fetchObj).then((result) => {
      if (result === 'error') {
        dispatch({
          type: INDICATOR_GRAPH_ERROR
        });
      } else {
        dispatch({
          type: GET_INDICATOR_HISTORICAL_DATA,
          payload: result.data
        });
      }
    });
  };
};

export const initializeIndicator = (indicatorId) => {
  return (dispatch) => {
    dispatch(resetIndicators());
    if (!indicatorId) {
      return;
    }
    dispatch({
      type: INITIATE_INDICATOR,
      payload: indicatorId
    });
    dispatch(getEvents(indicatorId));
  };
};