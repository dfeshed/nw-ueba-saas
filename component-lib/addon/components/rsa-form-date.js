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
    if (this.get('pikaday').getDate() === null || isEmpty(this.$().val())) {
      this.set('value', null);
      this.get('onSelection')(null);
    }

    this.get('onClose')();
  }
});
