const minutesInDay = 1440;
const minutesInHour = 60;
const DAY = 'DAY';
const HOUR = 'HOUR';
const MINUTE = 'MINUTE';
export const TIME_UNITS = [ DAY, HOUR, MINUTE ];

export function createDuration(value, unit) {
  if (!TIME_UNITS.includes(unit)) {
    throw `The "unit" argument must be one of the following: ${JSON.stringify(TIME_UNITS)}`;
  }
  return value + unit.toLowerCase()[0];
}

function getMinutes(duration) {
  let totalMinutes = 0;

  const days = duration.match(/(\d+)\s*d/);
  const hours = duration.match(/(\d+)\s*h/);
  const minutes = duration.match(/(\d+)\s*m/);
  if (days) {
    totalMinutes += parseInt(days[1], 10) * minutesInDay;
  }
  if (hours) {
    totalMinutes += parseInt(hours[1], 10) * minutesInHour;
  }
  if (minutes) {
    totalMinutes += parseInt(minutes[1], 10);
  }
  return totalMinutes;
}

export function parseDuration(duration, defaultDuration) {
  duration = duration || defaultDuration || '1h';
  const minutes = getMinutes(duration);

  let parsed;

  if (minutes % minutesInDay === 0) {
    parsed = { value: minutes / minutesInDay, unit: DAY };
  } else if (minutes % minutesInHour === 0) {
    parsed = { value: minutes / minutesInHour, unit: HOUR };
  } else {
    parsed = { value: minutes, unit: MINUTE };
  }

  return parsed;
}