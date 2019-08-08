const FILTER_TYPES = [
  {
    'name': 'name',
    'label': 'investigateHosts.downloads.filters.fileName',
    'type': 'text',
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'investigateHosts.downloads.errorMessages.invalidFilterInputLength'
      },
      format: {
        validator: (value) => {
          return /[<>:"'/|\\?]+/.test(value);
        },
        message: 'investigateHosts.downloads.errorMessages.invalidCharsAlphaNumericOnly'
      }
    },
    'placeholder': 'e.g. Filename.dll'
  },
  {
    'name': 'extension',
    'label': 'investigateHosts.downloads.filters.fileType',
    'type': 'text',
    'validations': {
      length: {
        validator: (value) => {
          return value.length > 256;
        },
        message: 'investigateHosts.downloads.errorMessages.invalidFilterInputLength'
      },
      format: {
        validator: (value) => {
          return /[<>:"'/|\\?]+/.test(value);
        },
        message: 'investigateHosts.downloads.errorMessages.invalidCharsAlphaNumericOnly'
      }
    },
    'placeholder': 'e.g. dll'
  },
  {
    name: 'timeStomping',
    label: 'investigateHosts.downloads.filters.timeStomping',
    type: 'list',
    listOptions: [
      { name: true, label: 'investigateHosts.downloads.filters.timeStomping' }
    ]
  },
  {
    name: 'creationTime',
    label: 'investigateHosts.downloads.filters.creationTimeFn',
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
  },
  {
    name: 'creationTimeSi',
    label: 'investigateHosts.downloads.filters.creationTimeSi',
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
  },
  {
    name: 'fileReadTime',
    label: 'investigateHosts.downloads.filters.fileReadTimeFn',
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
  },
  {
    name: 'fileReadTimeSi',
    label: 'investigateHosts.downloads.filters.fileReadTimeSi',
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
  },
  {
    name: 'mftChangedTime',
    label: 'investigateHosts.downloads.filters.mftChangedTimeFn',
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
  },
  {
    name: 'mftChangedTimeSi',
    label: 'investigateHosts.downloads.filters.mftChangedTimeSi',
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
  },
  {
    name: 'alteredTime',
    label: 'investigateHosts.downloads.filters.alteredTimeFn',
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
  },
  {
    name: 'alteredTimeSi',
    label: 'investigateHosts.downloads.filters.alteredTimeSi',
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