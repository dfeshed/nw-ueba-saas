import _ from 'lodash';

export const lookupCoreDevice = (services, eventSource) => {
  if (!eventSource) {
    return null;
  }
  const pattern = new RegExp('(.*):(.*)');
  const patternMatch = pattern.exec(eventSource);
  if (!patternMatch) {
    return null;
  }
  const [ , eventHost, eventPort ] = patternMatch;
  const lookup = _.filter(services, (service) => {
    const { host, port } = service;
    const hostFound = host && host == eventHost;
    const portFound = port && port == eventPort;
    const portFallbackFound = port && port - 6000 == eventPort;
    return hostFound && (portFound || portFallbackFound);
  });
  return lookup && lookup.length === 1 && lookup[0].id;
};
