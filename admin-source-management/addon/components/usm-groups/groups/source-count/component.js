import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

export default Component.extend({

  i18n: service(),

  @computed('item.sourceCount')
  srcCount(sourceCount) {
    const i18n = this.get('i18n');
    switch (sourceCount) {
      case -1:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNewGroupTooltip');
      case -2:
        return i18n.t('adminUsm.groups.list.sourceCountPublishedNoEndpointTooltip');
      case -3:
        return i18n.t('adminUsm.groups.list.sourceCountUnpublishedGroupTooltip');
      default:
        return sourceCount;
    }
  }
});