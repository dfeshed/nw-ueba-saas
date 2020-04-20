import moment from 'moment';
import _ from 'lodash';

const _isEmpty = (value) => {
  return value === null || value === undefined;
};

export const ERROR_CODES = {
  YEAR_IS_EMPTY: 'yearIsEmpty',
  MONTH_IS_EMPTY: 'monthIsEmpty',
  DATE_IS_EMPTY: 'dateIsEmpty',
  HOUR_IS_EMPTY: 'hourIsEmpty',
  MINUTE_IS_EMPTY: 'minuteIsEmpty',
  SECOND_IS_EMPTY: 'secondIsEmpty',
  MONTH_OUT_OF_BOUNDS: 'monthOutOfBounds',
  DATE_OUT_OF_BOUNDS: 'dateOutOfBounds',
  HOUR_OUT_OF_BOUNDS: 'hourOutOfBounds',
  HOUR_OUT_OF_BOUNDS_12_HOUR: 'hourOutOfBounds12Hour',
  MINUTE_OUT_OF_BOUNDS: 'minuteOutOfBounds',
  SECOND_OUT_OF_BOUNDS: 'secondOutOfBounds'
};

/**
 * Returns an array of error codes
 * @method validate
 * @param values An array of the date values, e.g., [1976, 1, 22, 17, 0, 9] for Feb 22, 1976 5:00:09 pm
 * @param use12HourClock
 * @returns {Array}
 * @public
 */
export const validate = (values, use12HourClock) => {
  const [year, month, date, hour, minute, second] = values;
  const yearError = validateYear(year);
  const monthError = validateMonth(month);
  const dateError = validateDate(year, month, date);
  const hourError = validateHour(hour, use12HourClock);
  const minuteError = validateMinute(minute);
  const secondError = validateSecond(second);
  return _.compact([yearError, monthError, dateError, hourError, minuteError, secondError]);
};

export const validateYear = (year) => {
  if (_isEmpty(year)) {
    return ERROR_CODES.YEAR_IS_EMPTY;
  }
};

export const validateMonth = (month) => {
  if (_isEmpty(month)) {
    return ERROR_CODES.MONTH_IS_EMPTY;
  }
  if (month < 0 || month > 11) {
    return ERROR_CODES.MONTH_OUT_OF_BOUNDS;
  }
};

export const validateDate = (year, month, date) => {
  if (_isEmpty(date)) {
    return ERROR_CODES.DATE_IS_EMPTY;
  }
  if (date < 1 || date > moment([year, month]).daysInMonth()) {
    return ERROR_CODES.DATE_OUT_OF_BOUNDS;
  }
};

export const validateHour = (hour, use12HourClock) => {
  if (_isEmpty(hour)) {
    return ERROR_CODES.HOUR_IS_EMPTY;
  }
  if (use12HourClock && (hour < 1 || hour > 12)) {
    return ERROR_CODES.HOUR_OUT_OF_BOUNDS_12_HOUR;
  } else if (hour > 23) {
    return ERROR_CODES.HOUR_OUT_OF_BOUNDS;
  }
};

export const validateMinute = (value) => {
  if (_isEmpty(value)) {
    return ERROR_CODES.MINUTE_IS_EMPTY;
  }
  if (value > 59) {
    return ERROR_CODES.MINUTE_OUT_OF_BOUNDS;
  }
};

export const validateSecond = (value) => {
  if (_isEmpty(value)) {
    return ERROR_CODES.SECOND_IS_EMPTY;
  }
  if (value > 59) {
    return ERROR_CODES.SECOND_OUT_OF_BOUNDS;
  }
};

