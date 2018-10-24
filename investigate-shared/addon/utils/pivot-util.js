import { serializeQueryParams } from 'investigate-shared/utils/query-utils';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { get } from '@ember/object';

const INVESTIGATE_META_MAPPING = {
  'machine.machineName': 'alias.host',
  'userName': ['username', 'user.dst', 'user.src'],
  'machineIpv4': ['ip.src', 'ip.dst', 'device.ip', 'alias.ip'],
  'machineIpv6': ['ipv6.src', 'ipv6.dst', 'device.ipv6', 'alias.ipv6'],
  'checksumSha256': 'checksum',
  'checksumMd5': 'checksum',
  'firstFileName': 'filename'
};

const SKIP_QUOTES = [ 'ip.src', 'ip.dst', 'ipv6.src', 'ipv6.dst', 'device.ip', 'device.ipv6', 'alias.ipv6', 'alias.ip' ];

const _buildFilter = (metaName, metaValue, itemList) => {
  const investigateMeta = INVESTIGATE_META_MAPPING[metaName];
  const value = metaValue || get(itemList[0], metaName); // if metaValue not passed get the value from itemList
  // If list meta then add || in query
  if (Array.isArray(investigateMeta)) {
    const query = investigateMeta.map((meta) => {
      return _getQuery(meta, value);
    });
    return query.join('||');
  }
  return _getQuery(investigateMeta, value);
};

const _getQuery = (metaName, metaValue) => {
  if (SKIP_QUOTES.includes(metaName)) {
    return `${metaName} = ${metaValue}`;
  }
  return `${metaName} = "${metaValue}"`;
};


/**
 * Opens the investigate page with events query
 * @private
 */
const navigateToInvestigateEventsAnalysis = ({ metaName, metaValue, itemList }, serviceId, timeRange, zoneId) => {
  const { value, unit } = timeRange;
  const { startTime, endTime } = buildTimeRange(value, unit, zoneId);

  const mf = _buildFilter(metaName, metaValue, itemList);
  const queryParams = {
    sid: serviceId, // Service Id
    mf: encodeURI(encodeURIComponent(mf)), // Meta filter
    st: startTime, // Stat time
    et: endTime, // End time
    mps: 'default', // Meta panel size
    rs: 'max' // Recon size
  };
  const query = serializeQueryParams(queryParams);
  const path = `${window.location.origin}/investigate/events?${query}}`;
  window.open(path);
};

/**
 * Opens the investigate/navigate page with query
 * @private
 */
const navigateToInvestigateNavigate = ({ metaName, metaValue, itemList }, serviceId, timeRange, zoneId) => {
  const { value, unit } = timeRange;
  const { startTime, endTime } = buildTimeRange(value, unit, zoneId);

  const mf = _buildFilter(metaName, metaValue, itemList);
  const baseURL = `${window.location.origin}/investigation/endpointid/${serviceId}/navigate/query`;
  const query = encodeURI(encodeURIComponent(mf));
  const path = `${baseURL}/${query}/date/${startTime}/${endTime}`;
  window.open(path);
};

export {
  navigateToInvestigateNavigate,
  navigateToInvestigateEventsAnalysis
};
