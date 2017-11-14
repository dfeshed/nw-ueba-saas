const DAY = 'DAY';
const HOUR = 'HOUR';
const MINUTE = 'MINUTE';
export const TIME_UNITS = [ DAY, HOUR, MINUTE ];

function determineUnit(shorthandUnit) {
  let unit;
  switch (shorthandUnit) {
    case 'd':
      unit = DAY;
      break;
    case 'm':
      unit = MINUTE;
      break;
    default:
      unit = HOUR;
      break;
  }
  return unit;
}

export function createDuration(value, unit) {
  if (!TIME_UNITS.includes(unit)) {
    throw `The "unit" argument must be one of the following: ${JSON.stringify(TIME_UNITS)}`;
  }
  return value + unit.toLowerCase()[0];
}

export function parseDuration(duration, defaultDuration) {
  duration = duration || defaultDuration || '1h';
  const parts = /^([\d]+)([\w]+)/.exec(duration);
  return parts ?
  {
    value: parseInt(parts[1], 10),
    unit: determineUnit(parts[2])
  } :
  null;
}