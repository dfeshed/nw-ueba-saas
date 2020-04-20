import { computed, action } from '@ember/object';
import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

/**
 * Extension of the Data Table default row class for supporting selecting and dragging of rows
 * @public
 */
const Row = Component.extend({
  tagName: 'td',
  i18n: service(),

  srcCountTooltip: computed('item.sourceCount', 'item.dirty', 'item.lastPublishedOn', function() {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, this.item?.dirty, this.item?.sourceCount, this.item?.lastPublishedOn);
  }),

  srcCount: computed('item.sourceCount', function() {
    return getSourceCount(this.item?.sourceCount);
  }),

  @action
  showTip(index) {
    document.getElementsByClassName('tip')[index].classList.add('show');
  },

  @action
  hideTip(index) {
    document.getElementsByClassName('tip')[index].classList.remove('show');
  }
});

export default Row;