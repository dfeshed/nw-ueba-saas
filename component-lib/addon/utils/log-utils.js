// The name of the custom event key that the UI uses to hold the event's log data. Acts as a pseudo meta key.
const META_KEY_LOG_DATA = 'log';

// The name of the meta key that indicates which events are of type Log.
const META_KEY_MEDIUM = 'medium';

// The value of the 'medium' meta key for Log-type events.
const META_VALUE_MEDIUM_LOG = 32;

// The name of the custom boolean key that the UI uses to flag events whose log data has been requested & is pending.
const UI_KEY_LOG_DATA_STATUS = 'logStatus';

// Returns true if a given event record is of type Log. This is determined by reading its 'medium' meta key value.
function isLogEvent(item) {
  return item[META_KEY_MEDIUM] === META_VALUE_MEDIUM_LOG;
}

// Returns true if a given event record already has log data stored in it. Inspects a specific meta key value.
function eventHasLogData(item) {
  return item[META_KEY_LOG_DATA] !== undefined;
}

// Returns true if the status of a given event record's is not set to 'resolved'
// or 'rejected'.
function eventLogDataIsPending(item) {
  const status = item[UI_KEY_LOG_DATA_STATUS];
  return status !== 'resolved' && status !== 'rejected';
}

export {
  isLogEvent,
  eventHasLogData,
  eventLogDataIsPending
};
