import _ from 'lodash';
import moment from 'moment';

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

/**
 * Returns the unix timestamp for a time in the past.
 *
 * Example:
 * 'LAST_FORTY_EIGHT_HOURS' will return the Unix Timestamp (milliseconds) for exactly 48 hours ago
 *
 * @param sinceWhen string The name of the since-when option (e.g., LAST_7_DAYS, ALL_TIME)
 * @returns {number} Unix Timestamp (milliseconds)
 * @public
 */
function resolveSinceWhenStartTime(sinceWhen) {
  const since = typeof sinceWhen === 'string' ? SINCE_WHEN_TYPES_BY_NAME[sinceWhen] : sinceWhen;
  if (!since || _.isUndefined(since.subtract) || _.isUndefined(since.unit)) {
    throw new Error(`resolveSinceWhenStartTime could not find the corresponding since-when option from ${sinceWhen}`);
  }
  // if the sinceWhen is for all time, just set the unix timestamp to be 0 (i.e., jan 1 1970), otherwise calculate
  return sinceWhen === 'ALL_TIME' ? 0 : moment().subtract(since.subtract, since.unit).valueOf();
}

export {
  SINCE_WHEN_TYPES,
  SINCE_WHEN_TYPES_BY_NAME,
  resolveSinceWhenStartTime
};