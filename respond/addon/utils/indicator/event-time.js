/**
 * Attempts to read the timestamp of a given NetWitness event object.
 *
 * Depending on its source, an event object doesn't always store its time in the same property name.  This function
 * attempts to encapsulate that.  Specifically it will first look for in the `time` property, which is assumed to
 * have the timestamp in long integer format.  If not found, it will then look in the `timestamp` property, which
 * may have the timestamp in a string format; if so, this function will attempt to convert the string to long integer.
 *
 * @param {number} [time] The NetWitness event object's `time` property value.
 * @param {number|string} [timestamp] The NetWitness event object's `timestamp` property value.
 * @returns {number|undefined} The timestamp as a number or undefined if unsuccessful.
 * @public
 */
export default function eventTime({ time, timestamp }) {
  if (time) {
    return time;
  }
  if (timestamp) {
    if (typeof(timestamp) === 'string') {
      timestamp = Number(new Date(timestamp));
    }
  }
  return isNaN(timestamp) ? undefined : timestamp;

}