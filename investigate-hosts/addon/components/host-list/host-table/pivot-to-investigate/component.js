import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import moment from 'moment';
import { next } from 'ember-runloop';
import injectService from 'ember-service/inject';
import get from 'ember-metal/get';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';

const INVESTIGATE_META_MAPPING = {
  'machine.machineName': 'alias.host'
};

const dispatchToActions = {
  getAllServices
};


const PivotToInvestigate = Component.extend({

  classNames: 'pivot-to-investigate',

  eventBus: injectService(),

  showServiceModal: false,

  metaName: null,

  serviceList: null,

  item: null,

  timeRange: {
    value: 24,
    unit: 'hours'
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

  _buildFilter(item) {
    const metaName = this.get('metaName');
    const investigateMeta = INVESTIGATE_META_MAPPING[metaName];
    const metaValue = get(item, metaName);
    return `${investigateMeta} = "${metaValue}"`;
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
   * @param serviceId
   * @param item
   * @private
   */
  _navigateToInvestigate(serviceId, item) {
    const { startTime, endTime } = this._buildTimeRange();
    const filter = this._buildFilter(item);
    const query = `${serviceId}/${startTime}/${endTime}/${encodeURIComponent(filter)}`;
    const path = `${window.location.origin}/investigate/events/query/${query}`;
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

    pivotToInvestigate() {
      this._navigateToInvestigate(this.get('selectedService'), this.get('item'));
    },

    onModalClose() {
      this.set('showServiceModal', false);
    }
  }
});

export default connect(null, dispatchToActions)(PivotToInvestigate);
