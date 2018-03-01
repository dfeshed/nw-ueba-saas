import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import moment from 'moment';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { get } from '@ember/object';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import $ from 'jquery';

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

  eventBus: service(),

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

  selectedService: null,

  @computed
  contextItems() {
    const cntx = this;
    return [
      {
        label: 'Pivot to Investigate',
        action() {
          cntx.send('toggleServiceSelection');
        }
      }
    ];
  },

  @computed('selectedService')
  isDisabled(selectedService) {
    return !selectedService;
  },

  @computed('serviceList')
  isLoadingServices(serviceList) {
    return !serviceList;
  },

  columnsConfig: [
    {
      field: 'displayName',
      title: 'Service Name',
      width: '50%'
    },
    {
      field: 'name',
      title: 'Service Type',
      width: '43%'
    }
  ],

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

  _buildTimeRange() {
    const { value, unit } = this.get('timeRange');
    const endTime = moment().endOf('minute');
    const startTime = moment(endTime).subtract(value, unit).add(1, 'minutes').startOf('minute');
    return {
      startTime: this._getTimezoneTime(startTime),
      endTime: this._getTimezoneTime(endTime)
    };
  },

  _getTimezoneTime(browserTime) {
    const { zoneId } = this.get('timezone.selected');
    const timeWithoutZone = moment(browserTime).parseZone(browserTime).format('YYYY-MM-DD HH:mm:ss'); // Removing browser timezone information
    const timeInUserTimeZone = moment.tz(timeWithoutZone, zoneId);
    return timeInUserTimeZone;
  },

  /**
   * Opens the investigate page with events query
   * @private
   */
  _navigateToInvestigateEventsAnalysis() {
    const selectedService = this.get('selectedService');
    const { startTime, endTime } = this._buildTimeRange();
    const mf = this._buildFilter();
    const queryParams = {
      sid: selectedService, // Service Id
      mf, // Meta filter
      st: startTime.tz('utc').format('X'), // Stat time
      et: endTime.tz('utc').format('X'), // End time
      mps: 'default', // Meta panel size
      rs: 'max' // Recon size
    };
    const path = `${window.location.origin}/investigate/events?${$.param(queryParams)}`;
    this._closeModal();
    window.open(path);
  },
  /**
   * Opens the investigate/navigate page with query
   * @private
   */
  _navigateToInvestigateNavigate() {
    const selectedService = this.get('selectedService');
    const { startTime, endTime } = this._buildTimeRange();
    const mf = this._buildFilter();
    const baseURL = `${window.location.origin}/investigation/endpointid/${selectedService}/navigate/query`;
    const query = encodeURIComponent(mf);
    const path = `${baseURL}/${query}/date/${startTime.tz('utc').format()}/${endTime.tz('utc').format()}`;
    this._closeModal();
    window.open(path);
  },

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-service-modal');
    this.set('showServiceModal', false);
    this.set('selectedService', null);
  },

  actions: {
    onToggleRowSelection(item, index, e, table) {
      table.set('selectedIndex', index);
      this.set('selectedService', item.id);
    },

    toggleServiceSelection() {
      this.set('showServiceModal', true);
      this.send('getAllServices');
      next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-service-modal');
      });
    },

    onCancel() {
      this._closeModal();
    },

    pivotToInvestigateEventAnalysis() {
      this._navigateToInvestigateEventsAnalysis();
    },

    pivotToInvestigateNavigate() {
      this._navigateToInvestigateNavigate();
    },

    onModalClose() {
      this.set('showServiceModal', false);
      this.set('selectedService', null);
    }
  }
});

export default connect(null, dispatchToActions)(PivotToInvestigate);
