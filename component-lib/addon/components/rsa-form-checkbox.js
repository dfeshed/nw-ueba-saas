import Ember from 'ember';
import layout from '../templates/components/rsa-form-checkbox';

export default Ember.Component.extend({

  layout,

  tagName: 'label',

  classNames: ['rsa-form-checkbox'],

  classNameBindings: ['isError',
                      'isDisabled',
                      'isReadOnly',
                      'isSelected',
                      'isActive'],

  label: null,

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
        if (!that.get('isDestroyed')) {
          that.set('isActive', true);
        }
      });
    });

    this.$('input').on('blur', function() {
      Ember.run.next(that, function() {
        if (!that.get('isDestroyed')) {
          that.set('isActive', false);
        }
      });
    });
  },

  change() {
    Ember.run.once(this, function() {
      if (!this.get('isReadOnly') && !this.get('isDisabled')) {
        if (this.$('input').is(':checked')) {
          this.set('value', true);
        } else {
          this.set('value', false);
        }
      }
    });
  },

  syncInput() {
    Ember.run.once(this, function() {
      let input = this.$('input');
      if (input) {
        if ((this.get('value') === false) || (this.get('value') === 'false')) {
          input.attr('checked', false);
        } else if ((this.get('value') === true) || (this.get('value') === 'true')) {
          input.attr('checked', true);
        }
      }
    });
  },

  isSelected: Ember.computed(function() {
    return (this.get('value') === true) || (this.get('value') === 'true');
  }).property('value')
});
