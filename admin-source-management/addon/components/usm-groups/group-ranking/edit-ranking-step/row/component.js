import computed from 'ember-computed-decorators';
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

  @computed('item.sourceCount', 'item.dirty', 'item.lastPublishedOn')
  srcCountTooltip(sourceCount, isDirty, lastPublishedOn) {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, isDirty, sourceCount, lastPublishedOn);
  },

  @computed('item.sourceCount')
  srcCount(sourceCount) {
    return getSourceCount(sourceCount);
  },

  actions: {
    showTip(index) {
      document.getElementsByClassName('tip')[index].classList.add('show');
    },
    hideTip(index) {
      document.getElementsByClassName('tip')[index].classList.remove('show');
    }
  }
});

export default Row;