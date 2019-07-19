import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches a list of protocol objects.
 * @public
 */
function fetchProtocolList() {
  const transport = lookup('service:transport');
  return transport.send('/logcollection', {
    message: 'ls'
  });
}

/**
 * Fetches the event rate for the selected protocol type
 * @public
 */
function fetchEventRate(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_events_rate`, {
    message: 'get'
  });
}

/**
 * Fetches the byte rate for the selected protocol type
 * @public
 */
function fetchByteRate(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_bytes_rate`, {
    message: 'get'
  });
}

/**
 * Fetches the error rate for the selected protocol type
 * @public
 */
function fetchErrorRate(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_errors_rate`, {
    message: 'get'
  });
}

/**
 * Fetches the number of events for the selected protocol type
 * @public
 */
function fetchTotalEvents(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_events`, {
    message: 'get'
  });
}

/**
 * Fetches the number of bytes for the selected protocol type
 * @public
 */
function fetchTotalBytes(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_bytes`, {
    message: 'get'
  });
}

/**
 * Fetches the number of errors for the selected protocol type
 * @public
 */
function fetchTotalErrors(protocolObj) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolObj.protocol}/stats/eventsources/total_errors`, {
    message: 'get'
  });
}

export default
{
  fetchProtocolList,
  fetchEventRate,
  fetchByteRate,
  fetchErrorRate,
  fetchTotalEvents,
  fetchTotalBytes,
  fetchTotalErrors
};