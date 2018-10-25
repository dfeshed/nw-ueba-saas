import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { navigateToInvestigateEventsAnalysis, navigateToInvestigateNavigate } from 'investigate-shared/utils/pivot-util';

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
          cntx.send('pivotToInvestigate');
        }
      }
    ];
  },

  _closeModal() {
    this.set('showServiceModal', false);
  },

  actions: {

    pivotToInvestigate() {
      const serviceId = this.get('serviceId');
      if (serviceId && serviceId !== '-1') {
        const {
          metaName,
          metaValue,
          item,
          timeRange
        } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

        const { zoneId } = this.get('timezone.selected');
        navigateToInvestigateEventsAnalysis({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
      } else {
        const serviceList = this.get('serviceList');
        if (!(serviceList && serviceList.length)) {
          this.send('getAllServices');
        }
        this.set('showServiceModal', true);
      }
    },

    onCancel() {
      this._closeModal();
    },

    pivotToInvestigateEventAnalysis(serviceId) {
      const {
        metaName,
        metaValue,
        item,
        timeRange
      } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

      const { zoneId } = this.get('timezone.selected');
      this._closeModal();
      navigateToInvestigateEventsAnalysis({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
    },

    pivotToInvestigateNavigate(serviceId) {
      const {
        metaName,
        metaValue,
        item,
        timeRange
      } = this.getProperties('metaName', 'metaValue', 'item', 'timeRange');

      const { zoneId } = this.get('timezone.selected');
      this._closeModal();
      navigateToInvestigateNavigate({ metaName, metaValue, itemList: [item] }, serviceId, timeRange, zoneId);
    },

    onModalClose() {
      this.set('showServiceModal', false);
    }
  }
});

export default connect(undefined, dispatchToActions)(PivotToInvestigate);
