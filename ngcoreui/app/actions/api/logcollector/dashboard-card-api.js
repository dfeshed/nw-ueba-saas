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

export default { fetchProtocolList };