/**
 * A set of types representing the time range since a starting point time (e.g., since 7 days ago)
 * @public
 * @type {[*]}
 */
const SINCE_WHEN_TYPES = [
  {
    'name': 'LAST_5_MINUTES',
    'unit': 'minutes',
    'subtract': 5
  },
  {
    'name': 'LAST_10_MINUTES',
    'unit': 'minutes',
    'subtract': 10
  },
  {
    'name': 'LAST_15_MINUTES',
    'unit': 'minutes',
    'subtract': 15
  },
  {
    'name': 'LAST_30_MINUTES',
    'unit': 'minutes',
    'subtract': 30
  },
  {
    'name': 'LAST_HOUR',
    'unit': 'minutes',
    'subtract': 60
  },
  {
    'name': 'LAST_3_HOURS',
    'unit': 'hours',
    'subtract': 3
  },
  {
    'name': 'LAST_6_HOURS',
    'unit': 'hours',
    'subtract': 6
  },
  {
    'name': 'LAST_TWELVE_HOURS',
    'unit': 'hours',
    'subtract': 12
  },
  {
    'name': 'LAST_TWENTY_FOUR_HOURS',
    'unit': 'days',
    'subtract': 1
  },
  {
    'name': 'LAST_FORTY_EIGHT_HOURS',
    'unit': 'days',
    'subtract': 2
  },
  {
    'name': 'LAST_5_DAYS',
    'unit': 'days',
    'subtract': 5
  },
  {
    'name': 'LAST_7_DAYS',
    'unit': 'days',
    'subtract': 7
  },
  {
    'name': 'LAST_14_DAYS',
    'unit': 'days',
    'subtract': 14
  },
  {
    'name': 'LAST_30_DAYS',
    'unit': 'days',
    'subtract': 30
  },
  {
    'name': 'ALL_TIME',
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