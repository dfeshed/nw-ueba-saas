import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { get } from '@ember/object';
import moment from 'moment';

const INVESTIGATE_META_MAPPING = {
  'machineIdentity.machineName': 'alias.host',
  'userName': ['username', 'user.dst', 'user.src'],
  'machineIpv4': ['ip.src', 'ip.dst', 'device.ip', 'alias.ip'],
  'machineIpv6': ['ipv6.src', 'ipv6.dst', 'device.ipv6', 'alias.ipv6'],
  'checksumSha256': ['checksum.all', 'filename.all'],
  'checksumMd5': 'checksum.all',
  'firstFileName': 'filename',
  'thumbprint': 'cert.thumbprint'
};

const SKIP_QUOTES = [ 'ip.src', 'ip.dst', 'ipv6.src', 'ipv6.dst', 'device.ip', 'device.ipv6', 'alias.ipv6', 'alias.ip' ];

// whether selected item has fileName as 'fileName' or 'firstFileName', meta vakue is fetched
const getFileName = (item) => {
  return get(item, 'fileName') || get(item, 'firstFileName');
};

const _escapeBackslash = (value) => {
  return value.replace(/\\\\?(?!')/g, '\\\\');
};

const _buildFilter = (metaName, metaValue, itemList) => {
  const investigateMeta = INVESTIGATE_META_MAPPING[metaName];
  let value = metaValue || get(itemList[0], metaName); // if metaValue not passed get the value from itemList

  if (metaName === 'userName') {
    value = _escapeBackslash(value);
  }

  // If list meta then add || in query
  if (Array.isArray(investigateMeta)) {
    const query = investigateMeta.map((meta) => {
      if (meta === 'filename.all') {
        return _getQuery(meta, getFileName(itemList[0]));
      }
      return _getQuery(meta, value);
    });
    return `(${ query.join('||') })`;
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
const navigateToInvestigateEventsAnalysis = ({ metaName, metaValue, itemList, additionalFilter }, serviceId, timeRange, zoneId) => {
  const { value, unit } = timeRange;
  const { startTime, endTime } = buildTimeRange(value, unit, zoneId);

  let mf = _buildFilter(metaName, metaValue, itemList);

  if (additionalFilter) {
    mf = `${mf} && ${additionalFilter}`;
  }

  const queryParams = {
    sid: serviceId, // Service Id
    mf: encodeURIComponent(mf), // Meta filter
    st: startTime, // Stat time
    et: endTime, // End time
    mps: 'default', // Meta panel size
    rs: 'max' // Recon size
  };
  const query = serializeQueryParams(queryParams);
  const path = `${window.location.origin}/investigate/events?${query}`;
  window.open(path);
};

/**
 * Opens the investigate/navigate page with query
 * @private
 */
const navigateToInvestigateNavigate = ({ metaName, metaValue, itemList, additionalFilter, startTime, endTime }, serviceId, timeRange, zoneId) => {

  let range = { startTime, endTime };
  if (timeRange) {
    const { value, unit } = timeRange;
    range = buildTimeRange(value, unit, zoneId);
  }

  let mf = _buildFilter(metaName, metaValue, itemList);

  if (additionalFilter) {
    mf = `${mf} && ${additionalFilter}`;
  }
  const baseURL = `${window.location.origin}/investigation/endpointid/${serviceId}/navigate/query`;
  const query = encodeURIComponent(mf);
  const path = `${baseURL}/${query}/date/${moment(range.startTime * 1000).tz('UTC').format()}/${moment(range.endTime * 1000).tz('UTC').format()}`;
  window.open(path);
};


/**
 * Creates a serialized representation of an array suitable for use in a URL query string.
 * @public
 */
const serializeQueryParams = (qp = []) => {
  return Object.entries(qp)
    .filter((d) => d[1] !== undefined) // filter out undefined values of properties
    .map((d) => `${d[0]}=${d[1]}`).join('&');
};

export {
  navigateToInvestigateNavigate,
  navigateToInvestigateEventsAnalysis,
  serializeQueryParams
};
