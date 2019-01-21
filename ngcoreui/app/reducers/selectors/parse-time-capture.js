export default (value) => {
  // this string is redonkulous and there is no actual time value
  value = value.split(',')[0];
  const weeks = parseFloat((/(\d+) week/.exec(value) || [])[1] || '0');
  const days = parseFloat((/(\d+) day/.exec(value) || [])[1] || '0');
  const hours = parseFloat((/(\d+) hour/.exec(value) || [])[1] || '0');
  const minutes = parseFloat((/(\d+) minute/.exec(value) || [])[1] || '0');
  const seconds = parseFloat((/(\d+) second/.exec(value) || [])[1] || '0');

  const duration = seconds + (60 * (minutes + (60 * (hours + (24 * (days + (7 * weeks)))))));
  if (duration === 0) {
    return { start: null, duration: null };
  }

  let durationStr = new Date(duration * 1000).toISOString().replace(/^[0-9-]*T/, '').replace(/\.\d*Z$/, '');

  if (weeks > 0 || days > 0) {
    const numDays = 7 * weeks + days;
    const s = numDays > 1 ? 's' : '';
    durationStr = `${numDays} day${s} ${durationStr}`;
  }

  const date = new Date((new Date()).getTime() - (duration * 1000));
  return { started: date.toLocaleString('en-US', { hour12: false }), duration: durationStr };

  // NOTE: use the following once rsa-content-datetime is used in the controls
  // const date = (new Date()).getTime() - (duration * 1000);
  // return { started: date, duration: durationStr };
};
