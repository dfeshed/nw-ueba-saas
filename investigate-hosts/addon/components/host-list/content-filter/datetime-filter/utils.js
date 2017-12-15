import moment from 'moment';

export const convertToTimeFormat = ({ value, unit }) => {
  return moment().subtract(value, unit).startOf('minute').valueOf();
};
// Converts the date time selected into the expected application timezone format.
export const getTimezoneTime = (browserTime, zoneId) => {
  const timeWithoutZone = moment(browserTime).parseZone(browserTime).format('YYYY-MM-DD HH:mm:ss'); // Removing browser timezone information
  const timeInUserTimeZone = moment.tz(timeWithoutZone, zoneId);
  return timeInUserTimeZone.valueOf();
};