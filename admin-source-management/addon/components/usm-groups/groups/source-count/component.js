import Component from '@ember/component';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

export default Component.extend({
  i18n: service(),

  srcCountTooltip: computed('item.sourceCount', 'item.dirty', 'item.lastPublishedOn', function() {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, this.item?.dirty, this.item?.sourceCount, this.item?.lastPublishedOn);
  }),

  srcCount: computed('item.sourceCount', function() {
    return getSourceCount(this.item?.sourceCount);
  })
});