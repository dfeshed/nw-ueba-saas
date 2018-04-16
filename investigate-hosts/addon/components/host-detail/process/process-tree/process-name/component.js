import Component from '@ember/component';
import { get, set } from '@ember/object';
import computed, { alias } from 'ember-computed-decorators';
import { htmlSafe } from '@ember/string';
import moment from 'moment';
import { inject as service } from '@ember/service';

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

  _buildTimeRange() {
    const endTime = moment().endOf('minute');
    const startTime = moment(endTime).subtract(1, 'days').add(1, 'minutes').startOf('minute');
    return {
      startTime: this._getTimezoneTime(startTime).unix(),
      endTime: this._getTimezoneTime(endTime).unix()
    };
  },

  _getTimezoneTime(browserTime) {
    const { zoneId } = this.get('timezone.selected');
    const timeWithoutZone = moment(browserTime).parseZone(browserTime).format('YYYY-MM-DD HH:mm:ss'); // Removing browser timezone information
    const timeInUserTimeZone = moment.tz(timeWithoutZone, zoneId);
    return timeInUserTimeZone;
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
      const { item, agentId } = this.getProperties('item', 'agentId');
      const { name, checksumSha256 } = item;
      const timeRange = this._buildTimeRange();
      const timeStr = `startTime=${timeRange.startTime}&endTime=${timeRange.endTime}`;
      const serviceId = '46afbb7c-1156-45fb-bc4c-b5143a529610&agentId'; // Will be removed
      const queryParams = `?checksum=${checksumSha256}&serviceId=${serviceId}=${agentId}&processName=${name}&${timeStr}`;

      window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1000,height=700');
    }
  }
});
