import moment from 'moment';

/**
 * Returns a four digit year value, and assumes that any single or double digit value is part of the 21st century
 * @method formatYear
 * @param val
 * @returns {string}
 * @public
 */
const formatYear = (val) => {
  let value = val;
  if (val >= 0 && val <= 99) { // single or double digits are assumed to be in the 21st century
    value = 2000 + val;
  }

  const date = moment([value, 1, 1]);
  const isValid = date.isValid();
  return isValid ? date.format('YYYY') : value;
};

/**
 * Returns a formatted value. No real validation occurs here. We want to see, however, that "1" becomes "01" for month
 * and day, and that the year always has 4 numbers.
 * @param value
 * @param type
 * @returns {*}
 * @public
 */
export const format = (value, type) => {
  let format;
  value = parseInt(value, 10); // ensure that the value is an integer

  if (isNaN(value)) { // if the value is not a number, set to null
    return '';
  }

  const val = Math.abs(value); // let's only deal in positive numbers
  if (type === 'year') {
    format = formatYear(val);
  } else {
    format = val < 10 ? `0${val}` : `${val}`;
  }
  return format;
};
