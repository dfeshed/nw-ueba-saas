/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const SINCE_WHEN_TYPES = [
  {
    'name': 'LAST_HOUR',
    'labelKey': 'respond.timeframeOptions.lastHour',
    'unit': 'minutes',
    'subtract': 60
  },
  {
    'name': 'LAST_TWELVE_HOURS',
    'labelKey': 'respond.timeframeOptions.lastTwelveHours',
    'unit': 'hours',
    'subtract': 12
  },
  {
    'name': 'LAST_TWENTY_FOUR_HOURS',
    'labelKey': 'respond.timeframeOptions.lastTwentyFourHours',
    'unit': 'days',
    'subtract': 1
  },
  {
    'name': 'LAST_FORTY_EIGHT_HOURS',
    'labelKey': 'respond.timeframeOptions.lastFortyEightHours',
    'unit': 'days',
    'subtract': 2
  },
  {
    'name': 'LAST_SEVEN_DAYS',
    'labelKey': 'respond.timeframeOptions.lastSevenDays',
    'unit': 'days',
    'subtract': 7
  },
  {
    'name': 'LAST_MONTH',
    'labelKey': 'respond.timeframeOptions.lastMonth',
    'unit': 'months',
    'subtract': 1
  },
  {
    'name': 'LAST_TWELVE_MONTHS',
    'labelKey': 'respond.timeframeOptions.lastTwelveMonths',
    'unit': 'months',
    'subtract': 12
  },
  {
    'name': 'ALL_TIME',
    'labelKey': 'respond.timeframeOptions.allTime',
    'unit': 'years',
    'subtract': 50
  }
];

const SINCE_WHEN_TYPES_BY_NAME = {};

SINCE_WHEN_TYPES.forEach((t) => SINCE_WHEN_TYPES_BY_NAME[t.name] = t);

export {
  SINCE_WHEN_TYPES,
  SINCE_WHEN_TYPES_BY_NAME
};