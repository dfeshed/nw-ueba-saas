import reselect from 'reselect';
import _ from 'lodash';

const _protocolsState = (state) => state.logcollector.dashboardCard;
const { createSelector } = reselect;

export const areProtocolsLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemKeysStatus === 'wait'
);

export const isProtocolDataLoadingSuccess = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemProtocolDataStatus === 'complete'
);

export const protocolArray = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemKeys
);

export const getProtocolData = createSelector(
  _protocolsState,
  (_protocolsState) => _.values(_protocolsState.itemProtocolData)
);

const getNumberFromLocaleNumberString = (number) => {
  return parseInt(number.replace(/,/g, ''), 10);
};

export const addHeaderRow = (protocolRowValues) => {
  if (protocolRowValues.length === 0) {
    return protocolRowValues;
  }
  const headerRow = {};
  headerRow.protocol = 'TOTAL';
  headerRow.eventRate = 0;
  headerRow.byteRate = 0;
  headerRow.errorRate = 0;
  headerRow.numOfEvents = 0;
  headerRow.numOfBytes = 0;
  headerRow.errorCount = 0;

  protocolRowValues.forEach((item) => {
    headerRow.eventRate += getNumberFromLocaleNumberString(item.eventRate);
    headerRow.byteRate += getNumberFromLocaleNumberString(item.byteRate);
    headerRow.errorRate += getNumberFromLocaleNumberString(item.errorRate);
    headerRow.numOfEvents += getNumberFromLocaleNumberString(item.numOfEvents);
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
