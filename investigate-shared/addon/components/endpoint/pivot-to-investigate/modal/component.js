import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { get } from '@ember/object';
import { serializeQueryParams } from 'investigate-shared/utils/query-utils';
import { buildTimeRange } from 'investigate-shared/utils/time-util';


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

export default Component.extend({
  layout,

  tagName: '',

  timezone: service(),

  showAsRightClick: false,

  metaName: null,

  metaValue: null,

  serviceList: null,

  item: null,

  investigateText: null,

  timeRange: {
    value: 2,
    unit: 'days'
  },

  onClose: null,

  _buildFilter() {
    const { metaName, metaValue, item } = this.getProperties('metaName', 'metaValue', 'item');
    const investigateMeta = INVESTIGATE_META_MAPPING[metaName];
    const value = metaValue || get(item, metaName); // if metaValue not passed get the value from item
    // If list meta then add || in query
    if (Array.isArray(investigateMeta)) {
      const query = investigateMeta.map((meta) => {
        return this._getQuery(meta, value);
      });
      return query.join('||');
    }
    return this._getQuery(investigateMeta, value);
  },


  _getQuery(metaName, metaValue) {
    if (SKIP_QUOTES.includes(metaName)) {
      return `${metaName} = ${metaValue}`;
    }
    return `${metaName} = "${metaValue}"`;
  },

  /**
   * Opens the investigate page with events query
   * @private
   */
  _navigateToInvestigateEventsAnalysis(serviceId) {
    const { zoneId } = this.get('timezone.selected');
    const { value, unit } = this.get('timeRange');
    const { startTime, endTime } = buildTimeRange(value, unit, zoneId);

    const mf = this._buildFilter();
    const queryParams = {
      sid: serviceId, // Service Id
      mf: encodeURI(encodeURIComponent(mf)), // Meta filter
      st: startTime.tz('utc').format('X'), // Stat time
      et: endTime.tz('utc').format('X'), // End time
      mps: 'default', // Meta panel size
      rs: 'max' // Recon size
    };
    const query = serializeQueryParams(queryParams);
    const path = `${window.location.origin}/investigate/events?${query}}`;
    this._closeModal();
    window.open(path);
  },
  /**
   * Opens the investigate/navigate page with query
   * @private
   */
  _navigateToInvestigateNavigate(serviceId) {
    const { zoneId } = this.get('timezone.selected');
    const { value, unit } = this.get('timeRange');
    const { startTime, endTime } = buildTimeRange(value, unit, zoneId);

    const mf = this._buildFilter();
    const baseURL = `${window.location.origin}/investigation/endpointid/${serviceId}/navigate/query`;
    const query = encodeURI(encodeURIComponent(mf));
    const path = `${baseURL}/${query}/date/${startTime.tz('utc').format()}/${endTime.tz('utc').format()}`;
    this._closeModal();
    window.open(path);
  },

  _closeModal() {
    const closeModal = this.get('closeModal');
    if (closeModal) {
      closeModal();
    }
  },

  actions: {

    onCancel() {
      this._closeModal();
    },

    pivotToInvestigateEventAnalysis(serviceId) {
      this._navigateToInvestigateEventsAnalysis(serviceId);
    },

    pivotToInvestigateNavigate(serviceId) {
      this._navigateToInvestigateNavigate(serviceId);
    }
  }
});
