import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { isJazzAgent } from 'investigate-hosts/reducers/details/process/selectors';
import { isEcatAgent } from 'investigate-hosts/reducers/details/overview/selectors';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const dispatchToActions = {
  getAllServices
};

const stateToComputed = (state) => ({
  isJazzAgent: isJazzAgent(state),
  isEcatAgent: isEcatAgent(state),
  serverId: state.endpointQuery.serverId,
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

const PivotToPA = Component.extend({

  showServiceModal: false,

  timezone: service(),

  eventBus: service(),

  features: service(),

  serviceList: null,

  agentId: null,

  osType: null,

  hostName: null,

  /**
   * context menu config for process analysis
   * @public
   */
  @computed
  contextItems() {
    const cntx = this;
    return [
      {
        label: 'Process Analysis',
        action([item]) {
          cntx.send('toggleServiceSelection', item);
        }
      }
    ];
  },

  @computed('osType', 'isJazzAgent', 'isEcatAgent')
  disabledContextMenu(osType, isJazzAgent, isEcatAgent) {
    return osType === 'linux' || isJazzAgent || isEcatAgent;
  },

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-service-modal');
    this.set('showServiceModal', false);
  },

  actions: {
    /**
     * navigate to process analysis page
     * @public
     */
    navigateToProcessAnalysis(serviceId) {
      const { zoneId } = this.get('timezone.selected');
      const {
        item,
        agentId,
        osType,
        hostName,
        timeRange } = this.getProperties('item', 'agentId', 'osType', 'hostName', 'timeRange');
      const { name, checksumSha256, vpid } = item;
      const { value, unit } = timeRange;
      const serverId = this.get('serverId');
      const range = buildTimeRange(value, unit, zoneId);
      const timeStr = `st=${range.startTime}&et=${range.endTime}`;
      const osTypeParam = `osType=${osType}&vid=${vpid}`;
      const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&hn=${hostName}&pn=${name}&${timeStr}&${osTypeParam}&serverId=${serverId}`;
      window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1440,height=900');
      this._closeModal();
    },

    toggleServiceSelection(item) {
      const serviceId = this.get('serviceId');
      this.set('item', item);
      if (serviceId) {
        this.send('navigateToProcessAnalysis', serviceId);
      } else {
        this.set('showServiceModal', true);

        this.send('getAllServices');
        next(() => {
          this.get('eventBus').trigger('rsa-application-modal-open-service-modal');
        });
      }
    },

    onCancel() {
      this._closeModal();
    },

    onModalClose() {
      this.set('showServiceModal', false);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(PivotToPA);
