import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import layout from './template';

export default Component.extend({
  layout,

  tagName: '',

  selectedService: null,

  modalTitle: null,

  onClose: null,

  eventId: 'service-modal',

  /** *
   * List of services to display
   * @public
   */
  serviceList: null,

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

  @computed('selectedService')
  isDisabled(selectedService) {
    return !selectedService;
  },

  @computed('serviceList')
  isLoadingServices(serviceList) {
    return !serviceList;
  },


  actions: {

    onRowSelection(item, index, e, table) {
      table.set('selectedIndex', index);
      this.set('selectedService', item);
    },

    onModalClose() {
      this.set('selectedService', null);
      const onClose = this.get('onClose');
      if (onClose) {
        onClose();
      }
    }
  }
});
