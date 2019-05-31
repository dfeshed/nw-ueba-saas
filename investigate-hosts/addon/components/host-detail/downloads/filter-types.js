const FILTER_TYPES = [
  {
    name: 'fileType',
    label: 'investigateHosts.downloads.filters.fileType',
    type: 'list',
    listOptions: [
      { name: 'Mft', label: 'investigateHosts.downloads.filters.mft' },
      { name: 'File', label: 'investigateHosts.downloads.filters.files' },
      { name: 'memory dump', label: 'investigateHosts.downloads.filters.memoryDump' }
    ]
  },
  {
    'name': 'filename',
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
    name: 'downloadedTime',
    label: 'investigateHosts.downloads.filters.downloadedTime',
    type: 'date',
    operator: 'GREATER_THAN',
    timeframes: [
      { name: 'IN_LAST_ONE_HOUR', selected: true, value: 1, unit: 'Hours' },
      { name: 'IN_LAST_THREE_HOURS', value: 3, unit: 'Hours' },
      { name: 'IN_LAST_SIX_HOURS', value: 6, unit: 'Hours' },
      { name: 'IN_LAST_TWELVE_HOURS', value: 12, unit: 'Hours' },
      { name: 'IN_LAST_TWENTY_FOUR_HOURS', value: 24, unit: 'Hours' },
      { name: 'IN_LAST_TWO_DAYS', value: 2, unit: 'Days' },
      { name: 'IN_LAST_SEVEN_DAYS', value: 7, unit: 'Days' },
      { name: 'IN_LAST_TWO_WEEKS', value: 14, unit: 'Days' },
      { name: 'IN_LAST_ONE_MONTH', value: 30, unit: 'Days' }
    ]
  }
];

export {
  FILTER_TYPES
};
