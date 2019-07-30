import { lookup } from 'ember-dependency-lookup';

/**
 * Fetches a list of protocol objects for log-collector card.
 * @public
 */
function fetchProtocolList() {
  const transport = lookup('service:transport');
  return transport.send('/logcollection', {
    message: 'ls'
  });
}

/**
 * Fetches the information of the required protocol for log-collector card.
 * @public
 */
function fetchProtocolData(protocolName) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolName}/stats/eventsources/`, {
    message: 'ls'
  });
}

/**
 * Fetches the information of the protocols for event sources card.
 * @public
 */
function fetchEventSourcesProtocolData() {
  const transport = lookup('service:transport');
  return transport.send('/event-processors/logdecoder/stats/eventsources/queue', {
    message: 'ls',
    params: {
      depth: '2'
    }
  });
}
export default
{
  fetchProtocolList,
  fetchProtocolData,
  fetchEventSourcesProtocolData
};