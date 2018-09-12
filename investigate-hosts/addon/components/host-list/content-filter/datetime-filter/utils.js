import moment from 'moment';

// Converts the date time selected into the expected application timezone format.
export const getTimezoneTime = (time, zoneId) => {
  const browserTime = Array.isArray(time) ? time[0] : time;
  const timeWithoutZone = moment(browserTime).parseZone(browserTime).format('YYYY-MM-DD HH:mm:ss'); // Removing browser timezone information
  const timeInUserTimeZone = moment.tz(timeWithoutZone, zoneId);
  return timeInUserTimeZone.valueOf();
};

export const getSelectedTimeOption = (configOptions, { value, relativeValueType }) => {
  return configOptions.filter((item) => (item.value === value) && (item.unit === relativeValueType));
};