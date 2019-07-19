import reselect from 'reselect';

const _protocolsState = (state) => state.logcollector.dashboardCard;
const { createSelector } = reselect;

export const areProtocolsLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemsStatus === 'wait'
);

export const isProtocolEventRateLoadingFailed = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemEventRateStatus === 'error'
);

export const isProtocolEventRateLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemEventRateStatus === 'wait'
);

export const isProtocolByteRateLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemByteRateStatus === 'wait'
);

export const isProtocolErrorRateLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemErrorRateStatus === 'wait'
);

export const isProtocolNumEventsLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemTotalEventsStatus === 'wait'
);

export const isProtocolNumBytesLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemTotalBytesStatus === 'wait'
);

export const isProtocolNumErrorsLoading = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemTotalErrorsStatus === 'wait'
);

export const allProtocolColumnDataLoadingSuccess = (state) => {
  return !isProtocolEventRateLoading(state) &&
    !isProtocolEventRateLoadingFailed(state) &&
    !isProtocolByteRateLoading(state) &&
    !isProtocolErrorRateLoading(state) &&
    !isProtocolNumEventsLoading(state) &&
    !isProtocolNumBytesLoading(state) &&
    !isProtocolNumErrorsLoading(state);
};

export const buildProtocolRow = (state) => {
  const eventRateDict = eventRate(state);
  const byteRateDict = byteRate(state);
  const errorsRateDict = errorRate(state);
  const numEventsDict = numOfEvents(state);
  const numBytesDict = numOfBytes(state);
  const numErrorsDict = numOfErrors(state);

  let allProtocols = [];
  allProtocols.push(...Object.keys(eventRateDict), ...Object.keys(byteRateDict),
    ...Object.keys(errorsRateDict), ...Object.keys(numEventsDict), ...Object.keys(numBytesDict),
    ...Object.keys(numErrorsDict));
  allProtocols = [...new Set(allProtocols)];

  const items = [];
  for (let i = 0; i < allProtocols.length; i++) {
    const item = {};
    const key = allProtocols[i];
    item.protocol = key;
    item.eventRate = eventRateDict[key] != null ? eventRateDict[key].toLocaleString() : '0';
    item.byteRate = byteRateDict[key] != null ? byteRateDict[key].toLocaleString() : '0';
    item.errorRate = errorsRateDict[key] != null ? errorsRateDict[key].toLocaleString() : '0';
    item.numOfEvents = numEventsDict[key] != null ? numEventsDict[key].toLocaleString() : '0';
    item.numOfBytes = numBytesDict[key] != null ? numBytesDict[key].toLocaleString() : '0';
    item.errorCount = numErrorsDict[key] != null ? numErrorsDict[key].toLocaleString() : '0';
    items.push(item);
  }

  return items;
};

export const protocolArray = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemKeys
);

export const eventRate = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueEventRate
);

export const byteRate = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueByteRate
);

export const errorRate = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueErrorRate
);

export const numOfEvents = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueNumEvents
);

export const numOfBytes = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueNumBytes
);

export const numOfErrors = createSelector(
  _protocolsState,
  (_protocolsState) => _protocolsState.itemValueNumErrors
);