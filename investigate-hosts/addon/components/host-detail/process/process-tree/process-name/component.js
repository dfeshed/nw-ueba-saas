import Component from '@ember/component';
import { get, set } from '@ember/object';
import computed, { alias } from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import { next } from '@ember/runloop';
import { getAllServices } from 'investigate-hosts/actions/data-creators/host';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

import { buildTimeRange } from 'investigate-shared/utils/time-util';

const BASE_PADDING = 30;

const dispatchToActions = {
  getAllServices
};


const ProcessName = Component.extend({

  timezone: service(),

  eventBus: service(),

  classNames: ['process-name-column'],

  agentId: null,

  showServiceModal: false,

  serviceList: null,

  /**
   * To show the icon in th UI
   * @public
   */
  @alias('item.expanded')
  isExpanded: false,

  /**
   * Calculate the padding for the row based on the `level` property. Using this to achieve tree structure in the UI.
   * For each row `level` property set which indicates the depth of tree node.
   * @param item
   * @returns {*}
   * @public
   */
  @computed('item')
  style(item) {
    const left = BASE_PADDING * item.level;
    return htmlSafe(`padding-left: ${left}px;`);
  },
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
        action() {
          cntx.send('toggleServiceSelection');
        }
      }
    ];
  },

  _closeModal() {
    this.get('eventBus').trigger('rsa-application-modal-close-service-modal');
    this.set('showServiceModal', false);
  },

  actions: {
    toggleExpand() {
      const { item, index } = this.getProperties('item', 'index');
      set(item, 'expanded', !get(item, 'expanded'));
      this.onToggleExpand(index, item.level, item);
    },

    /**
     * navigate to process analysis page
     * @public
     */
    navigateToProcessAnalysis(serviceId) {
      const { zoneId } = this.get('timezone.selected');
      const { item, agentId } = this.getProperties('item', 'agentId');
      const { name, checksumSha256 } = item;
      const timeRange = buildTimeRange(1, 'days', zoneId);
      const timeStr = `st=${timeRange.startTime.unix()}&et=${timeRange.endTime.unix()}`;
      const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&pn=${name}&${timeStr}`;

      window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1440,height=900');
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

    onModalClose() {
      this.set('showServiceModal', false);
    }
  }
});
export default connect(null, dispatchToActions)(ProcessName);
