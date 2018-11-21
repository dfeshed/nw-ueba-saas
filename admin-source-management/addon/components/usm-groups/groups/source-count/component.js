import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { sourceCountTooltip, getSourceCount } from 'admin-source-management/utils/groups-util';

export default Component.extend({

  i18n: service(),

  @computed('item.sourceCount', 'item.dirty', 'item.lastPublishedOn')
  srcCountTooltip(sourceCount, isDirty, lastPublishedOn) {
    const i18n = this.get('i18n');
    return sourceCountTooltip(i18n, isDirty, sourceCount, lastPublishedOn);
  },

  @computed('item.sourceCount')
  srcCount(sourceCount) {
    return getSourceCount(sourceCount);
  }
});