import Ember from 'ember';
import layout from '../templates/components/rsa-form-checkbox';

const {
  Component,
  run,
  computed
} = Ember;

export default Component.extend({

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
      run.next(that, function() {
        if (!that.get('isDestroyed')) {
          that.set('isActive', true);
        }
      });
    });

    this.$('input').on('blur', function() {
      run.next(that, function() {
        if (!that.get('isDestroyed')) {
          that.set('isActive', false);
        }
      });
    });
  },

  change() {
    run.once(this, function() {
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
    run.once(this, function() {
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

  isSelected: computed('value', function() {
    return (this.get('value') === true) || (this.get('value') === 'true');
  })
});
