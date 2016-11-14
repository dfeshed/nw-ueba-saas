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

  classNameBindings: [
    'isError',
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

  /*
   * Set to false if you are going to track/control
   * the checkbox's value from outside this component.
   *
   * Setting to false prevents change events being fired
   * after a bound value is updated from parent component.
   */
  trackOwnValue: true,

  didInsertElement() {
    const that = this;
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

  /*
   * Reset checked when new attrs come in
   */
  didReceiveAttrs() {
    this.syncInput();
  },

  change() {
    if (this.get('trackOwnValue')) {
      run.once(this, function() {
        if (!this.get('isReadOnly') && !this.get('isDisabled')) {
          if (this.$('input').is(':checked')) {
            this.set('value', true);
          } else {
            this.set('value', false);
          }
        }
      });
    }
  },

  syncInput() {
    run.once(this, function() {
      const input = this.$('input');
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
