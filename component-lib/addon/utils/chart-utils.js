import d3 from 'd3';

const formatMillisecond = d3.timeFormat('.%L'); // .123
const formatSecond = d3.timeFormat('%I:%M:%S'); // 01:15:05
const formatSecond24 = d3.timeFormat('%H:%M:%S'); // 13:15:05
const formatMinute = d3.timeFormat('%I:%M'); // 01:15
const formatMinute24 = d3.timeFormat('%H:%M'); // 13:15
const formatHour = d3.timeFormat('%I:%M %p'); // 01:00 PM
const formatHour24 = d3.timeFormat('%H:%M'); // 13:00
const formatDay = d3.timeFormat('%a %d'); // Tue 02
const formatWeek = d3.timeFormat('%b %d'); // Jan 02
const formatMonth = d3.timeFormat('%B'); // January
const formatYear = d3.timeFormat('%Y'); // 2016

/**
 * Will find the minimum value within an Array of Arrays.
 * @public
 * @param data An Array of Arrays i.e. [[], []]
 * @param accessorFn A function run against each Object in an Array to retrieve the property of interest
 * @return The mimimum value
 */
export function min(data, accessorFn = undefined) {
  return d3.min(data.map((d) => d3.min(d, accessorFn)));
}

/**
 * Will find the maximum value within an Array of Arrays.
 * @public
 * @param data An Array of Arrays i.e. [[], []]
 * @param accessorFn A function run against each Object in an Array to retrieve the property of interest
 * @return The maximum value
 */
export function max(data, accessorFn = undefined) {
  return d3.max(data.map((d) => d3.max(d, accessorFn)));
}

/**
 * Computes the minimum and maximum values of an Array of Arrays
 * @public
 * @param data An Array of Arrays i.e. [[], []]
 * @param accessorFn A function run against each Object in an Array to retrieve the property of interest
 * @param zeroed Boolean indicating if the extent should be zero-based. i.e. [0, N]
 * @return A 2 element Array with min/max values
 */
export function computeExtent(data, accessorFn, zeroed) {
  return [zeroed ? 0 : min(data, accessorFn), max(data, accessorFn)];
}

/**
 * Creates a scale with the domain and range set. Values that fall out the
 * domain will be clamped to the domain min/max.
 * @public
 * @param scaleFn A scalaing function
 * @param domain The data extent
 * @param range The pixel range with which to map the domain
 * @return An instance of `scaleFn`
 */
export function createScale(scaleFn, domain, range) {
  return scaleFn().domain(domain).range(range).clamp(true);
}

// Calculates width minus left/right margin. Used for sizing the drawing
// area within some margin for axes, legend, text, etc.
export function calcGraphWidth(width, marginLeft, marginRight) {
  return width - marginLeft - marginRight;
}

// Calculates height minus top/bottom margin. Used for sizing the drawing
// area within some margin for axes, legend, text, etc.
export function calcGraphHeight(height, marginTop, marginBottom) {
  return height - marginTop - marginBottom;
}

/**
 * Generates a date format function that is a multi-scale tick format,
 * meaning that it formats times differently depending on the time.
 * For example, the start of February is formatted as "February",
 * while February second is formatted as "Feb 2". Has a minimum
 * precision of a minute.
 * @public
 * @param date The date to format
 * @return A format that most closely matches a date boundry
 */
export function multiDateFormat(date) {
  return (d3.timeMinute(date) < date ? formatSecond :
    d3.timeHour(date) < date ? formatMinute :
    d3.timeDay(date) < date ? formatHour :
    d3.timeMonth(date) < date ? (d3.timeWeek(date) < date ? formatDay : formatWeek) :
    d3.timeYear(date) < date ? formatMonth :
    formatYear)(date);
}

/**
 * Generates a 24 hour date format function that is a multi-scale tick format,
 * meaning that it formats times differently depending on the time.
 * For example, the start of February is formatted as "February",
 * while February second is formatted as "Feb 2". Has a minimum
 * precision of a minute.
 * @public
 * @param date The date to format
 * @return A format that most closely matches a date boundry
 */
export function multiDate24Format(date) {
  return (d3.timeMinute(date) < date ? formatSecond24 :
    d3.timeHour(date) < date ? formatMinute24 :
    d3.timeDay(date) < date ? formatHour24 :
    d3.timeMonth(date) < date ? (d3.timeWeek(date) < date ? formatDay : formatWeek) :
    d3.timeYear(date) < date ? formatMonth :
    formatYear)(date);
}

/**
 * Same as `multiDateFormat`, but to millisecond precision.
 * @public
 * @param date The date to format
 * @return A format that most closely matches a date boundry
 */
export function multiDateMsFormat(date) {
  return (d3.timeSecond(date) < date ? formatMillisecond :
    d3.timeMinute(date) < date ? formatSecond :
    d3.timeHour(date) < date ? formatMinute :
    d3.timeDay(date) < date ? formatHour :
    d3.timeMonth(date) < date ? (d3.timeWeek(date) < date ? formatDay : formatWeek) :
    d3.timeYear(date) < date ? formatMonth :
    formatYear)(date);
}

/**
 * Same as `multiDate24Format`, but to millisecond precision.
 * @public
 * @param date The date to format
 * @return A format that most closely matches a date boundry
 */
export function multiDateMs24Format(date) {
  return (d3.timeSecond(date) < date ? formatMillisecond :
    d3.timeMinute(date) < date ? formatSecond24 :
    d3.timeHour(date) < date ? formatMinute24 :
    d3.timeDay(date) < date ? formatHour24 :
    d3.timeMonth(date) < date ? (d3.timeWeek(date) < date ? formatDay : formatWeek) :
    d3.timeYear(date) < date ? formatMonth :
    formatYear)(date);
}

/**
 * Generates a number format function suitable for displaying a tick value,
 * automatically computing the appropriate precision based on the fixed
 * interval between tick values. The specified count should have the same
 * value as the count that is used to generate the tick values.
 * @public
 * @param domain An Array with min/max values for the scale
 * @param count The desired number of scale ticks
 * @return A SI-prefix format based on the largest value in the domain
 */
export function siFormat(domain, count = 10) {
  const specifier = d3.formatSpecifier('s');
  const step = d3.tickStep(...domain, count);
  const value = Math.max(...domain);
  const precision = d3.precisionPrefix(step, value);
  if (!specifier.precision && !isNaN(precision)) {
    specifier.precision = precision;
  }
  return d3.formatPrefix(specifier, value);
}