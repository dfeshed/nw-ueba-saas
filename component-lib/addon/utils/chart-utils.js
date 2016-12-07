import { formatPrefix, formatSpecifier, precisionPrefix } from 'd3-format';
import { max, min, tickStep } from 'd3-array';
import { timeDay, timeHour, timeMinute, timeMonth, timeWeek, timeYear } from 'd3-time';
import { timeFormat, utcFormat } from 'd3-time-format';

/**
 * Will find the minimum value within an Array of Arrays.
 * @public
 * @param  {Array}    data       An Array of Arrays i.e. [[], []]
 * @param  {Function} accessorFn A function run against each Object in an Array
 *                               to retrieve the property of interest
 * @return {Number}              The mimimum value
 */
export function minimum(data, accessorFn = undefined) {
  return min(data.map((d) => min(d, accessorFn)));
}

/**
 * Will find the maximum value within an Array of Arrays.
 * @public
 * @param  {Array}    data       An Array of Arrays i.e. [[], []]
 * @param  {Function} accessorFn A function run against each Object in an Array
 *                               to retrieve the property of interest
 * @return {Number}              The maximum value
 */
export function maximum(data, accessorFn = undefined) {
  return max(data.map((d) => max(d, accessorFn)));
}

/**
 * Computes the minimum and maximum values of an Array of Arrays.
 * @public
 * @param  {Array}    data       An Array of Arrays i.e. [[], []]
 * @param  {Function} accessorFn A function run against each Object in an Array
 *                               to retrieve the property of interest
 * @param  {Boolean}  zeroed     Should the extent be zero-based. i.e. [0, N]
 * @return {Array}               A 2 element Array with min/max values
 */
export function computeExtent(data, accessorFn, zeroed) {
  return [zeroed ? 0 : minimum(data, accessorFn), maximum(data, accessorFn)];
}

/**
 * Creates a scale with the domain and range set. Values that fall out the
 * domain will be clamped to the domain min/max.
 * @public
 * @param  {Function} scaleFn A scalaing function
 * @param  {Array}    domain  The data extent
 * @param  {Array}    range   The pixel range with which to map the domain
 * @return {Function}         An instance of `scaleFn`
 */
export function createScale(scaleFn, domain, range) {
  return scaleFn().domain(domain).range(range).clamp(true);
}

/**
 * Calculates width minus left/right margin. Used for sizing the drawing area
 * within some margin for axes, legend, text, etc.
 * @public
 * @param  {Number} width       Width of graph
 * @param  {Number} marginLeft  Left margin
 * @param  {Number} marginRight Right margin
 * @return {Number}             The graph width
 */
export function calcGraphWidth(width, marginLeft, marginRight) {
  return width - marginLeft - marginRight;
}

/**
 * Calculates height minus top/bottom margin. Used for sizing the drawing area
 * within some margin for axes, legend, text, etc.
 * @public
 * @param  {Number} height       Height of graph
 * @param  {Number} marginTop    Top margin
 * @param  {Number} marginBottom Bottom margin
 * @return {Number}              The graph height
 */
export function calcGraphHeight(height, marginTop, marginBottom) {
  return height - marginTop - marginBottom;
}

/**
 * Generates a date format function for a given time format (24 vs 12 hour
 * days), and timezone. Current implementation is limited to either UTC or
 * the local timezone of the operating system.
 * @public
 * @param  {Boolean} is24Hour Should the time portion reflect a 24-hour clock
 * @param  {String}  timezone Timezone of date.
 * @return {function}         A function for formatting dates.
 */
export function dateFormat(is24Hour, timezone) {
  const localeFormatFn = _localeFormat(timezone);
  if (is24Hour) {
    return function(date) {
      return (timeMinute(date) < date ? localeFormatFn('%H:%M:%S') : // 13:15:05
        timeHour(date) < date ? localeFormatFn('%H:%M') : // 13:15
        timeDay(date) < date ? localeFormatFn('%H:%M') : // 13:00
        timeMonth(date) < date ? (timeWeek(date) < date ? localeFormatFn('%a %d') : localeFormatFn('%b %d')) : // Tue 02 or Jan 02
        timeYear(date) < date ? localeFormatFn('%B') : // January
        localeFormatFn('%Y'))(date); // 2016
    };
  } else {
    return function(date) {
      return (timeMinute(date) < date ? localeFormatFn('%I:%M:%S') : // 01:15:05
        timeHour(date) < date ? localeFormatFn('%I:%M %p') : // 01:15 PM
        timeDay(date) < date ? localeFormatFn('%I:%M %p') : // 01:00 PM
        timeMonth(date) < date ? (timeWeek(date) < date ? localeFormatFn('%a %d') : localeFormatFn('%b %d')) : // Tue 02 or Jan 02
        timeYear(date) < date ? localeFormatFn('%B') : // January
        localeFormatFn('%Y'))(date); // 2016
    };
  }
}

/**
 * Generates a number format function suitable for displaying a tick value,
 * automatically computing the appropriate precision based on the fixed
 * interval between tick values. The specified count should have the same
 * value as the count that is used to generate the tick values.
 * @public
 * @param  {Array}  domain An Array with min/max values for the scale
 * @param  {Number} count  The desired number of scale ticks
 * @return {Function}      A SI-prefix format based on the largest value in the
 *                         domain
 */
export function siFormat(domain, count = 10) {
  const specifier = formatSpecifier('s');
  const step = tickStep(...domain, count);
  const value = Math.max(...domain);
  const precision = precisionPrefix(step, value);
  if (!specifier.precision && !isNaN(precision)) {
    specifier.precision = precision;
  }
  return formatPrefix(specifier, value);
}

/**
 * Returns a function that will format a date to a given timezone. Eventually,
 * this will support all timezones, but for now it only supports UTC and the
 * local timezone.
 * @private
 * @param  {String} timezone Timezone of date to format
 * @return {Function}        A function that formats to a given timezone
 */
function _localeFormat(timezone) {
  return (timezone === 'UTC') ? utcFormat : timeFormat;
}