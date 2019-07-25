export const PROTOCOL_DATA_EXTRACTED = {
  protocol: 'odbc',
  eventRate: '0',
  byteRate: '0',
  errorRate: '0',
  numOfEvents: '0',
  numOfBytes: '0',
  errorCount: '686'
};

export const PROTOCOL_ROW_VALUES = [
  {
    protocol: 'odbc',
    eventRate: '0',
    byteRate: '0',
    errorRate: '0',
    numOfEvents: '0',
    numOfBytes: '0',
    errorCount: '686'
  },
  {
    protocol: 'file',
    eventRate: '100',
    byteRate: '20',
    errorRate: '30',
    numOfEvents: '40',
    numOfBytes: '50',
    errorCount: '100'
  }
];

export const PROTOCOL_ROW_VALUES_EXPECTED = [
  {
    protocol: 'TOTAL',
    eventRate: '100',
    byteRate: '20',
    errorRate: '30',
    numOfEvents: '40',
    numOfBytes: '50',
    errorCount: '786'
  },
  PROTOCOL_ROW_VALUES[0],
  PROTOCOL_ROW_VALUES[1]
];

