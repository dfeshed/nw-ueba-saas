import Helper from 'ember-intl/helpers/t';
import { assert } from '@ember/debug';
import { getOwner } from '@ember/application';

export default Helper.extend({
  allowEmpty: true,

  format(key, options) {
    assert('[ember-intl] translation lookup attempted but no translation key was provided.', key);
    return this.i18n.t(key, options);
  },

  init() {
    this._super();
    this.i18n = getOwner(this).lookup('service:i18n');
    this.i18n.on('localeChanged', this, this.recompute);
  },

  willDestroy() {
    this._super();
    this.i18n.off('localeChanged', this, this.recompute);
  }

});