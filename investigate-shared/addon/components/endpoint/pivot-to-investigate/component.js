import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,

  classNames: 'actionbar-pivot-to-investigate',

  metaName: null,

  serviceList: null,

  item: null,

  getAllServices: null,

  timeRange: {
    value: 2,
    unit: 'days'
  },

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

  actions: {
    showModal() {
      this.set('showServiceModal', true);
    },
    closeModal() {
      this.set('showServiceModal', false);
    }
  }
});
