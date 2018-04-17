import moment from 'moment';

/**
 * Convert browser time to user selected timezone
 * @param browserTime
 * @param zoneId
 * @returns {*}
 * @public
 */
export const getTimezoneTime = (browserTime, zoneId) => {
  const timeWithoutZone = moment(browserTime).parseZone(browserTime).format('YYYY-MM-DD HH:mm:ss'); // Removing browser timezone information
  const timeInUserTimeZone = moment.tz(timeWithoutZone, zoneId);
  return timeInUserTimeZone;
};

/**
 * Returns the time-range converting relative time ex: 2days.
 * Value will be number and unit will be days or week
 * @param value
 * @param unit
 * @returns {{startTime: *, endTime: *}}
 * @public
 */
export const buildTimeRange = (value, unit, zoneId) => {
  const endTime = moment().endOf('minute');
  const startTime = moment(endTime).subtract(value, unit).add(1, 'minutes').startOf('minute');
  return {
    startTime: getTimezoneTime(startTime, zoneId),
    endTime: getTimezoneTime(endTime, zoneId)
  };
};
