import Component from '@ember/component';
import layout from './template';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

export default Component.extend({
  layout,
  classNames: ['tool-tip-note'],
  i18n: service(),

  @computed()
  label() {
    const i18n = this.get('i18n');
    const labelMessage = i18n.t('rsaTooltip.note');
    return labelMessage;
  },

  @computed()
  labelMessage() {
    const i18n = this.get('i18n');
    const labelMessage = i18n.t('rsaTooltip.labelMessage');
    return labelMessage;
  }

});
