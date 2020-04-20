import Helper from '@ember/component/helper';

/**
 * Timestomping is a technique that modifies the timestamps of a file (the modify, access, create, and change times),
 * often to mimic files that are in the same folder.
 *
 * This helper gives the flag true if the given field has timestomped.
 * @param params
 * @returns {*}
 */
export function isTimeStomped(params) {
  if (params.length > 0) {
    const [ field, timeField ] = params;
    const { creationTimeStomped, alteredTimeStomped, fileReadTimeStomped, mftChangedTimeStomped } = field;

    const timeStomp = {
      creationTime: creationTimeStomped,
      creationTimeSi: creationTimeStomped,
      alteredTime: alteredTimeStomped,
      alteredTimeSi: alteredTimeStomped,
      fileReadTime: fileReadTimeStomped,
      fileReadTimeSi: fileReadTimeStomped,
      mftChangedTime: mftChangedTimeStomped,
      mftChangedTimeSi: mftChangedTimeStomped
    };

    return timeStomp[timeField] || false;
  }
  return false;
}
export default Helper.helper(isTimeStomped);