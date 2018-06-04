import TIME_RANGES from 'investigate-shared/constants/time-ranges';

/**
 * Given startTime and endTime, this calculates number of days, hours and minutes and matches that with the RANGES array from time-ranges.js
 * to find the matching id. (eg.'LAST_30_MINUTES')
 * @param {number} startTime
 * @param {number} endTime
 * @return {object}
 * @private
 */
export const getTimeRangeIdFromRange = (startTime, endTime) => {
  const seconds = (endTime - startTime) + 1;
  const rangeObj = _getDaysHrsMinsFromSecs(seconds);
  let unit, value;
  let count = 0;
  for (const prop in rangeObj) {
    if (rangeObj[prop] !== 0) {
      unit = prop;
      value = rangeObj[prop];
      ++count;
    }
  }
  // if the rangeObj has more than one non-zero property, the query must be an ALL_DATA query from classic.
  if (count > 1) {
    return TIME_RANGES.ALL_DATA;
  }
  const getMatchingRange = (unit, value) => TIME_RANGES.RANGES.find((d) => (d.unit === unit && d.value === value));
  const range = getMatchingRange(unit, value);
  return range ? range.id : TIME_RANGES.DEFAULT_TIME_RANGE_ID;
};

/**
 * Given the seconds, it calculates the number of months, days, hours and minutes.
 * @param {number} seconds
 * @return {object}
 * @private
 */
const _getDaysHrsMinsFromSecs = (s) => {
  let h, mi, mo, d;
  mi = Math.floor(s / 60);
  s = s % 60;
  h = Math.floor(mi / 60);
  mi = mi % 60;
  d = Math.floor(h / 24);
  h = h % 24;
  // if number of days is 30, we are considering it as a month for our timeRange calculations.
  if (d === 30 && h === 0 && mi === 0) {
    mo = 1;
    d = 0;
  } else {
    mo = 0;
  }
  return { months: mo, days: d, hours: h, minutes: mi };
};
