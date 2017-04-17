import Ember from 'ember';
import PikadayMixin from '../mixins/pikaday';

const {
  Component,
  isEmpty
} = Ember;

export default Component.extend(PikadayMixin, {
  tagName: 'input',

  attributeBindings: [
    'readonly',
    'tabindex',
    'disabled',
    'placeholder',
    'type',
    'name',
    'size',
    'required',
    'title',
    'hidden'
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
