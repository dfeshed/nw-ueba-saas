import reselect from 'reselect';
import _ from 'lodash';
import { getNumberFromLocaleNumberString } from 'ngcoreui/reducers/logcollector/dashboard-card/dashboard-card-selectors';

const _esStatsState = (state) => state.logcollector.dashboardCard;
const { createSelector } = reselect;

export const isEsStatsDataLoadingSuccess = createSelector(
  _esStatsState,
  (_esStatsState) => _esStatsState.esStatsDataStatus === 'complete'
);

export const getEsStatsData = createSelector(
  _esStatsState,
  (_esStatsState) => _.values(_esStatsState.esStatsData)
);

export const addHeaderRow = (protocolRowValues) => {
  if (protocolRowValues.length === 0) {
    return protocolRowValues;
  }
  const headerRow = {};
  headerRow.protocol = 'TOTAL';
  headerRow.numOfEvents = 0;
  headerRow.eventRate = 0;
  headerRow.numOfBytes = 0;
  headerRow.errorCount = 0;

  protocolRowValues.forEach((item) => {
    headerRow.numOfEvents += getNumberFromLocaleNumberString(item.numOfEvents);
    headerRow.eventRate += getNumberFromLocaleNumberString(item.eventRate);
    headerRow.numOfBytes += getNumberFromLocaleNumberString(item.numOfBytes);
    headerRow.errorCount += getNumberFromLocaleNumberString(item.errorCount);
  });

  for (const key in headerRow) {
    if ((typeof headerRow[key]) === 'number') {
      headerRow[key] = headerRow[key].toLocaleString();
    }
  }

  protocolRowValues.unshift(headerRow);
  return protocolRowValues;
};

export const getEventSourcesStatsData = (state) => {
  let eventSourcesStatsRowValues = [];
  if (isEsStatsDataLoadingSuccess(state)) {
    eventSourcesStatsRowValues = getEsStatsData(state);
    eventSourcesStatsRowValues = addHeaderRow(eventSourcesStatsRowValues);
  }
  return eventSourcesStatsRowValues;
};

export const getEventSourcesTotalEventRate = (state) => {
  const protocolRowValues = getEsStatsData(state);
  let eventRate = 0;
  protocolRowValues.forEach((item) => {
    eventRate += getNumberFromLocaleNumberString(item.eventRate);
  });
  return eventRate.toString();
};
