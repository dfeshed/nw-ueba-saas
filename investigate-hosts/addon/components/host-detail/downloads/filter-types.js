const FILTER_TYPES = [
  {
    name: 'fileType',
    label: 'investigateHosts.downloads.filters.fileType',
    type: 'list',
    listOptions: [
      { name: 'mft', label: 'investigateHosts.downloads.filters.mft' },
      { name: 'files', label: 'investigateHosts.downloads.filters.files' },
      { name: 'memory dump', label: 'investigateHosts.downloads.filters.memoryDump' }
    ]
  },
  {
    'name': 'fileName',
    'label': 'investigateHosts.downloads.filters.fileName',
    'type': 'text',
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'investigateHosts.downloads.filters.errorMessages.invalidFilterInputLength'
      },
      format: {
        validator: (value) => {
          return /[<>:"'/|\\?]+/.test(value);
        },
        message: 'investigateHosts.downloads.filters.errorMessages.invalidCharsAlphaNumericOnly'
      }
    },
    'placeholder': 'e.g., Filename.dll'
  },
  {
    'name': 'checksum',
    'label': 'investigateHosts.downloads.filters.checksum',
    'type': 'text',
    'validations': {
      format: {
        validator: (value) => {
          return !/^[A-Za-z0-9]*$/.test(value);
        },
        message: 'investigateHosts.downloads.filters.errorMessages.invalidCharsAlphaNumericOnly'
      }
    }
  },
  {
    name: 'requestTime',
    label: 'investigateHosts.downloads.filters.requestTime',
    type: 'date',
    timeframes: [
      { name: 'LAST_FIVE_MINUTES', value: 5, unit: 'Minutes' },
      { name: 'LAST_TEN_MINUTES', value: 10, unit: 'Minutes' },
      { name: 'LAST_FIFTEEN_MINUTES', value: 15, unit: 'Minutes' },
      { name: 'LAST_THIRTY_MINUTES', value: 30, unit: 'Minutes' },
      { name: 'LAST_ONE_HOUR', value: 1, unit: 'Hours' },
      { name: 'LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { name: 'LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { name: 'LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { name: 'LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { name: 'LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { name: 'LAST_SEVEN_DAYS', value: 7, unit: 'Days' }
    ]
  }
];

export {
  FILTER_TYPES
};
