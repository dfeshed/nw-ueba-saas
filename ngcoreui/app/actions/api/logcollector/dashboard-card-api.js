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
 * Fetches the information of the required protocol.
 * @public
 */
function fetchProtocolData(protocolName) {
  const request = lookup('service:transport');
  return request.send(`/logcollection/${protocolName}/stats/eventsources/`, {
    message: 'ls'
  });
}

export default
{
  fetchProtocolList,
  fetchProtocolData
};