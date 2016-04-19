import Ember from 'ember';
import layout from '../templates/components/rsa-form-radio';

export default Ember.Component.extend({

  layout,

  tagName: 'label',

  classNames: ['rsa-form-radio'],

  classNameBindings: ['isError',
                      'isDisabled',
                      'isReadOnly',
                      'isSelected',
                      'isActive'],

  label: null,

  model: null,

  value: null,

  name: null,

  isError: false,

  isDisabled: false,

  isReadOnly: false,

  isActive: false,

  didInsertElement() {
    this.syncInput();

    let that = this;
    this.$('input').on('focus', function() {
      Ember.run.next(that, function() {
        that.set('isActive', true);
      });
    });

    this.$('input').on('blur', function() {
      Ember.run.next(that, function() {
        that.set('isActive', false);
      });
    });
  },

  change() {
    if (!this.get('isReadOnly') && !this.get('isDisabled')) {
      if (this.$('input').is(':checked')) {
        Ember.run.next(this, function() {
          this.set('model', this.get('value'));
        });
      }
    }
  },

  syncInput() {
    let input = this.$('input');

    if (input) {
      if (this.get('model') !== this.get('value')) {
        input.attr('checked', false);
      } else {
        input.attr('checked', true);
      }
    }
  },

  isSelected: Ember.computed(function() {
    this.syncInput();
    return this.get('model') === this.get('value');
  }).property('model', 'value')
});
