import { INITIATE_INDICATOR, GET_INDICATOR_EVENTS, GET_INDICATOR_HISTORICAL_DATA } from './types';
import { fetchData } from './fetch/data';
import { eventFilter, getIncidentKey } from 'entity-details/reducers/indicators/selectors';
import indicatorHistoricalDataMap from 'entity-details/utils/indicator-historical-data-map';

export const getEvents = (indicatorId) => {
  return (dispatch, getState) => {
    fetchData('indicatorEvents', eventFilter(getState()), null, indicatorId).then(({ data }) => {
      dispatch({
        type: GET_INDICATOR_EVENTS,
        payload: data
      });
    });
  };
};

export const getHistoricalData = (indicatorId) => {
  return (dispatch, getState) => {
    fetchData('indicatorEvents', indicatorHistoricalDataMap[getIncidentKey(getState())], null, indicatorId).then(({ data }) => {
      dispatch({
        type: GET_INDICATOR_HISTORICAL_DATA,
        payload: data
      });
    });
  };
};

export const initializeIndicator = (indicatorId) => {
  return (dispatch) => {
    dispatch({
      type: INITIATE_INDICATOR,
      payload: indicatorId
    });
    dispatch(getEvents(indicatorId));
    dispatch(getHistoricalData(indicatorId));
  };
};