import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import { navigateToInvestigateEventsAnalysis, navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';

export default Component.extend({
  layout,

  tagName: '',

  timezone: service(),

  showAsRightClick: false,

  metaName: null,

  metaValue: null,

  serviceList: null,

  itemList: null,

  investigateText: null,

  timeRange: {
    value: 2,
    unit: 'days'
  },

  onClose: null,

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
      const {
        metaName,
        metaValue,
        itemList,
        timeRange
      } = this.getProperties('metaName', 'metaValue', 'itemList', 'timeRange');

      const { zoneId } = this.get('timezone.selected');

      this._closeModal();
      navigateToInvestigateEventsAnalysis({ metaName, metaValue, itemList }, serviceId, timeRange, zoneId);
    },

    pivotToInvestigateNavigate(serviceId) {
      const {
        metaName,
        metaValue,
        itemList,
        timeRange
      } = this.getProperties('metaName', 'metaValue', 'itemList', 'timeRange');

      const { zoneId } = this.get('timezone.selected');
      this._closeModal();
      navigateToInvestigateNavigate({ metaName, metaValue, itemList }, serviceId, timeRange, zoneId);
    }
  }
});
