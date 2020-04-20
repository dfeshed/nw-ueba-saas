import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { groupByAlphabets } from 'recon/utils/meta-grouping-util';

export default Component.extend({
  layout,
  classNames: ['recon-meta-content-group'],

  @computed('groupType', 'metaItems')
  groupedMetaItems(groupType, metaItems) {
    if (groupType === 'alphabet') {
      return groupByAlphabets(metaItems);
    }
    return metaItems;
  }
});
