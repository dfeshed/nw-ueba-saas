import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { get } from '@ember/object';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { serializeQueryParams } from 'investigate-shared/utils/query-utils';

import { buildTimeRange } from 'investigate-shared/utils/time-util';


const INVESTIGATE_META_MAPPING = {
  'machine.machineName': 'alias.host',
  'userName': ['username', 'user.dst', 'user.src'],
  'machineIpv4': ['ip.src', 'ip.dst', 'device.ip', 'alias.ip'],
  'machineIpv6': ['ipv6.src', 'ipv6.dst', 'device.ipv6', 'alias.ipv6']
};

const SKIP_QUOTES = [ 'ip.src', 'ip.dst', 'ipv6.src', 'ipv6.dst', 'device.ip', 'device.ipv6', 'alias.ipv6', 'alias.ip' ];

const dispatchToActions = {
  getAllServices
};


const PivotToInvestigate = Component.extend({

  tagName: 'span',

  classNames: 'pivot-to-investigate',

  timezone: service(),

  showAsRightClick: false,

  showServiceModal: false,

  metaName: null,

  metaValue: null,

  serviceList: null,

  item: null,

  investigateText: null,

  timeRange: {
    value: 2,
    unit: 'days'
  },


  @computed
  contextItems() {
    const cntx = this;
    return [
      {
        label: 'Analyze Events',
        action() {
          cntx.send('toggleServiceSelection');
        }
      }
    ];
  },

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
    this.set('showServiceModal', false);
  },

  actions: {

    toggleServiceSelection() {
      const serviceList = this.get('serviceList');
      if (!(serviceList && serviceList.length)) {
        this.send('getAllServices');
      }
      this.set('showServiceModal', true);
    },

    onCancel() {
      this._closeModal();
    },

    pivotToInvestigateEventAnalysis(serviceId) {
      this._navigateToInvestigateEventsAnalysis(serviceId);
    },

    pivotToInvestigateNavigate(serviceId) {
      this._navigateToInvestigateNavigate(serviceId);
    },

    onModalClose() {
      this.set('showServiceModal', false);
    }
  }
});

export default connect(undefined, dispatchToActions)(PivotToInvestigate);
