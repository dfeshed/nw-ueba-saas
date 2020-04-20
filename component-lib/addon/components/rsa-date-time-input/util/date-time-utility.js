import moment from 'moment';

/**
 * Function that takes an array of date part values and a timezone, and returns a unix (ms) timestamp or null if the
 * date is invalid.
 *
 * @param values An array of the date parts in the following order [year, month, date, hour, minute, second] or an obj with
 * those properties defined (e.g., { year: 1976, month: 1, date: 22, hour: 17, minute: 0, second: 9 } )
 * @param timezone The timezone used to calculate the timestamp
 * @returns the unix timestamp (millisecond precision) for the provided date, or null if invalid
 * @public
 */
export const getTimestamp = (values, timezone) => {
  const momentDate = moment.tz(values, timezone);
  return momentDate.isValid() ? momentDate.valueOf() : null;
};

/**
 * Parses a string integer value and returns a positive integer, or null if the value is NaN
 * @method parseDAtePart
 * @param value
 * @returns int (positive) or null if not a number
 * @public
 */
export const parseDatePart = (value) => {
  const val = parseInt(value, 10); // ensure that the value is an integer
  return isNaN(val) ? null : Math.abs(val); // ensure that null is returned for non-numbers, and we only have positive vals
};

/**
 * Converts a timestamp and a timezone into an object of date parts, with properties for year, month, date, hour, minute,
 * second, and amPm
 * @method getDateParts
 * @param timestamp
 * @param timezone
 * @param use12HourClock
 * @returns {{year: *, month: *, date: *, hour: *, minute: *, second: *, amPm: string}}
 * @public
 */
export const getDateParts = (timestamp, timezone, use12HourClock) => {
  const momentDate = moment.tz(timestamp, timezone);
  const hour = momentDate.hour();
  return {
    year: momentDate.year(),
    month: momentDate.month(),
    date: momentDate.date(),
    hour: use12HourClock ? convertHourTo12HourClock(hour) : hour,
    minute: momentDate.minute(),
    second: momentDate.second(),
    amPm: hour > 12 ? 'pm' : 'am'
  };
};

/**
 * Converts a 12 hour clock value into a 24 hour clock value
 * @method convertHourTo24HourClock
 * @param hour an integer between 1 and 12
 * @param amPm either 'pm' or 'am'
 * @returns {number} between 0 and 23
 * @public
 */
export const convertHourTo24HourClock = (hour, amPm) => {
  if (amPm === 'am') {
    return hour === 12 ? 0 : hour; // only adjustment is 12am to 0 hour
  } else {
    return hour === 12 ? 12 : hour + 12; // 12 is already pm, stays the same, otherwise add 12
  }
};

/**
 * Converts a 24 hour clock value into a 12 hour clock value
 * @method convertHourTo12HourClock
 * @param hour an integer between 0 and 23
 * @returns {number} between 1 and 12
 * @public
 */
export const convertHourTo12HourClock = (hour) => {
  if (hour === 0) {
    return 12;
  }
  if (hour > 12) {
    return hour - 12;
  }
  return hour;
};