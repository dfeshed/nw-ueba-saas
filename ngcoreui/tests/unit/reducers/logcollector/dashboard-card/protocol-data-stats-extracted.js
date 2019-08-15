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

export const PROTOCOL_STATS_EXTRACTED = {
  protocol: 'syslog',
  numOfEvents: '0',
  eventRate: '0',
  numOfBytes: '0',
  errorCount: '0'
};

export const PROTOCOL_STATS_ROW_VALUES = [
  {
    protocol: 'syslog',
    numOfEvents: '10',
    eventRate: '20',
    numOfBytes: '0',
    errorCount: '30'
  },
  {
    protocol: 'file',
    numOfEvents: '100',
    eventRate: '0',
    numOfBytes: '200',
    errorCount: '20'
  }
];

export const PROTOCOL_STATS_VALUES_EXPECTED = [
  {
    protocol: 'TOTAL',
    numOfEvents: '110',
    eventRate: '20',
    numOfBytes: '200',
    errorCount: '50'
  },
  PROTOCOL_STATS_ROW_VALUES[0],
  PROTOCOL_STATS_ROW_VALUES[1]
];

export const PROTOCOL_ODBC_STATS = {
  protocol: 'odbc',
  numOfEvents: '10',
  eventRate: '20',
  numOfBytes: '30',
  errorCount: '40'
};

export const PROTOCOL_ROW_VALUES_2 = [
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

export const PROTOCOL_STATS_ROW_VALUES_2 = [
  {
    protocol: 'syslog',
    numOfEvents: '10',
    eventRate: '20',
    numOfBytes: '0',
    errorCount: '30'
  },
  {
    protocol: 'file',
    numOfEvents: '100',
    eventRate: '0',
    numOfBytes: '200',
    errorCount: '20'
  }
];
