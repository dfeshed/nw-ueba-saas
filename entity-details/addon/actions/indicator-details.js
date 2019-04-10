import { INITIATE_INDICATOR, GET_INDICATOR_EVENTS, GET_INDICATOR_HISTORICAL_DATA, RESET_INDICATOR } from './types';
import { fetchData } from './fetch/data';
import { eventFilter } from 'entity-details/reducers/indicators/selectors';

export const resetIndicators = () => ({ type: RESET_INDICATOR });

export const getEvents = (indicatorId) => {
  return (dispatch, getState) => {
    const fetchObj = {
      restEndpointLocation: 'indicatorEvents',
      data: eventFilter(getState()),
      method: 'GET',
      urlParameters: indicatorId
    };
    fetchData(fetchObj).then(({ data, total }) => {
      dispatch({
        type: GET_INDICATOR_EVENTS,
        payload: { data, total }
      });
      dispatch(getHistoricalData(indicatorId));
    });
  };
};

export const getHistoricalData = (indicatorId) => {
  return (dispatch) => {
    const fetchObj = {
      restEndpointLocation: 'historicalData',
      data: null,
      method: 'GET',
      urlParameters: indicatorId
    };
    fetchData(fetchObj).then(({ data }) => {
      dispatch({
        type: GET_INDICATOR_HISTORICAL_DATA,
        payload: data
      });
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