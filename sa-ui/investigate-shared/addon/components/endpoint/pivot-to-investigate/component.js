import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';
import { inject as service } from '@ember/service';

import { navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';

export default Component.extend({
  layout,

  classNames: 'actionbar-pivot-to-investigate',

  timezone: service(),

  metaName: null,

  serviceList: null,

  itemList: null,

  getAllServices: null,

  selectedService: null,

  showOnlyIcons: false,

  showServiceModal: false,

  @computed('selectedService')
  isDisabled(selectedService) {
    return !selectedService;
  },

  @computed('serviceList')
  isLoadingServices(serviceList) {
    return !serviceList;
  },

  init() {
    this._super(...arguments);
    this.timeRange = this.timeRange || {
      value: 7,
      unit: 'days'
    };
  },

  actions: {

    pivotToInvestigate() {
      const serviceId = this.get('serviceId');
      if (serviceId !== '-1') {
        const {
          metaName,
          metaValue,
          itemList,
          timeRange
        } = this.getProperties('metaName', 'metaValue', 'itemList', 'timeRange');

        const { zoneId } = this.get('timezone.selected');
        navigateToInvestigateNavigate({ metaName, metaValue, itemList }, serviceId, timeRange, zoneId);
      } else {
        this.set('showServiceModal', true);
      }
    },

    closeModal() {
      this.set('showServiceModal', false);
    }
  }
});
