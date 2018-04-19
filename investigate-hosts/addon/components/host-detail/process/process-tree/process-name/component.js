import Component from '@ember/component';
import { get, set } from '@ember/object';
import computed, { alias } from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import { inject as service } from '@ember/service';

import { buildTimeRange } from 'investigate-shared/utils/time-util';

const BASE_PADDING = 30;

export default Component.extend({

  timezone: service(),

  classNames: ['process-name-column'],

  agentId: null,

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
          cntx.send('navigateToProcessAnalysis');
        }
      }
    ];
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
    navigateToProcessAnalysis() {
      const { zoneId } = this.get('timezone.selected');
      const { item, agentId } = this.getProperties('item', 'agentId');
      const { name, checksumSha256 } = item;
      const timeRange = buildTimeRange(1, 'days', zoneId);
      const timeStr = `st=${timeRange.startTime.unix()}&et=${timeRange.endTime.unix()}`;
      const serviceId = '46afbb7c-1156-45fb-bc4c-b5143a529610'; // Will be removed
      const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&pn=${name}&${timeStr}`;

      window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1440,height=900');
    }
  }
});
