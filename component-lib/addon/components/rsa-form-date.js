import Component from '@ember/component';
import { isEmpty } from '@ember/utils';
import PikadayMixin from '../mixins/pikaday';

export default Component.extend(PikadayMixin, {
  tagName: 'input',

  attributeBindings: [
    'disabled',
    'hidden',
    'name',
    'placeholder',
    'readonly',
    'required',
    'size',
    'tabindex',
    'title',
    'type'
  ],

  type: 'text',

  didInsertElement() {
    this._super(...arguments);
    this.set('field', this.element);
    this.setupPikaday();
  },

  onPikadayOpen() {
    this.get('onOpen')();
  },

  onPikadayClose() {
    const pikadayValue = this.get('pikaday').getDate();
    const val = this.$().val();

    if (this.get('pikaday').getDate() === null || isEmpty(val)) {
      this.set('value', null);
      this.sendAction('onCloseAction', null);
      this.get('onSelection')(null);
    } else {
      this.set('value', val);
      this.sendAction('onCloseAction', pikadayValue);
    }

    this.get('onClose')(isEmpty(val) ? null : pikadayValue);
  }

});
