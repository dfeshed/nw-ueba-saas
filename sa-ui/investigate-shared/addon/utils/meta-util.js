import moment from 'moment';
import { getSrcFilename } from 'component-lib/utils/meta-util';

const META_MAP = {
  'device.type': 'device_type',
  'category': 'category',
  'time': 'timestamp',
  'action': 'action',
  'alias.host': 'hostname',
  'user': 'user_account',
  'owner': 'user_account',
  'checksum.src': 'file_SHA256',
  'ip.all': 'IP',
  'user.all': 'user',
  'size': 'size',
  'sessionid': 'event_source_id',
  'agent.id': 'agent_id',
  'process.vid.src': 'process_vid',
  'filename': 'filename',
  'checksum': 'checksum'
};

const OS_TYPE_SUPPORTED = ['windows', 'linux', 'mac'];

/* Both md5 and Sha256 checksum values are present in the same meta key. If Sha256 is present, use it.
   Otherwise, use md5
 */
const _hasSha256 = (hash) => {
  return hash && hash.length === 64;
};

const _prepareQueryString = (newEvent) => {
  const relatedLinks = [];
  const timestamp = `${newEvent.timestamp.split('+')[0]}Z`;
  // Seconds are not considered in the investigate query, hence increment the end time by a second so that current event is included in the result
  let endTime = moment(timestamp).endOf('minute').add(1, 'seconds');
  let startTime = moment(endTime);

  startTime = startTime.subtract(10, 'minutes').toISOString();
  endTime = moment(endTime).toISOString();

  endTime = endTime.replace(/:/g, '%3A');
  startTime = startTime.replace(/:/g, '%3A');

  relatedLinks[0] = {
    type: 'investigate_original_event',
    url: `/investigation/host/${newEvent.event_source}/navigate/event/AUTO/${newEvent.event_source_id}` };
  if (newEvent.hostname) {
    relatedLinks[1] = {
      type: 'investigate_destination_domain',
      url: `/investigation/${newEvent.event_source}/navigate/query/alias.host%3D'${newEvent.hostname}'%2Fdate%2F${startTime}%2F${endTime}` };
  }
  return relatedLinks;
};

export const transform = (event) => {
  const newEvent = {};
  const { metas } = event;
  if (event && metas) {
    let forwardIp = '';
    const source = {};
    const srcUser = {};
    const destination = {};

    const len = (metas && metas.length) || 0;
    for (let i = 0; i < len; i++) {
      const meta = metas[i];
      const [metaName, metaValue] = meta;
      const newName = META_MAP[metaName];
      if (newName) {
        newEvent[newName] = metaValue;
      }

      switch (metaName) {
        case 'checksum.src':
          if (!_hasSha256(source.hash)) {
            source.hash = metaValue;
          }
          break;
        case 'checksum.dst':
          if (!_hasSha256(destination.hash)) {
            destination.hash = metaValue;
          }
          break;
        case 'param.src':
          source.launch_argument = metaValue;
          break;
        case 'param.dst':
          destination.launch_argument = metaValue;
          break;
        case 'directory.src':
          source.path = metaValue;
          break;
        case 'directory.dst':
          destination.path = metaValue;
          break;
        case 'filename.dst':
          destination.filename = metaValue;
          break;
        case 'user.src':
          srcUser.username = metaValue;
          break;
        case 'forward.ip':
          forwardIp = metaValue;
          break;
        case 'OS':
          if (OS_TYPE_SUPPORTED.includes(metaValue)) {
            newEvent.operating_system = metaValue;
          }
          break;
      }
    }
    newEvent.type = 'Endpoint';
    newEvent.domain = newEvent.hostname;
    newEvent.event_source = `${forwardIp}:50005`;
    source.filename = getSrcFilename(metas);
    source.user = srcUser;
    newEvent.source = source;
    newEvent.destination = destination;
    newEvent.related_links = _prepareQueryString(newEvent);
    const data = [];
    data[0] = {
      hash: newEvent.checksum || source.hash,
      filename: newEvent.filename || source.filename
    };
    newEvent.data = data;
  }
  return newEvent;
};