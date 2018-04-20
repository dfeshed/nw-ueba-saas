import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import moment from 'moment';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { get } from '@ember/object';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-files/actions/data-creators';
import $ from 'jquery';

const INVESTIGATE_META_MAPPING = {
  'checksumSha256': 'checksum',
  'checksumMd5': 'checksum',
  'firstFileName': 'filename'
};

const dispatchToActions = {
  getAllServices
};


const PivotToInvestigate = Component.extend({

  classNames: 'pivot-to-investigate',

  eventBus: service(),

  showServiceModal: false,

  metaName: null,

  serviceList: null,

  item: null,

  timeRange: {
    value: 2,
    unit: 'days'
  },

  selectedService: null,

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
    const { metaName, item } = this.getProperties('metaName', 'item');
    const investigateMeta = INVESTIGATE_META_MAPPING[metaName];
    const value = get(item, metaName); // if metaValue not passed get the value from item
    return `${investigateMeta} = "${value}"`;
  },

  _buildTimeRange() {
    const { value, unit } = this.get('timeRange');
    const endTime = moment().endOf('minute');
    const startTime = moment(endTime).subtract(value, unit).add(1, 'minutes').startOf('minute');
    return {
      startTime,
      endTime
    };
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
    }
  }
});

export default connect(null, dispatchToActions)(PivotToInvestigate);
