import Ember from 'ember';
import layout from '../templates/components/rsa-form-radio';

const {
  Component,
  run,
  computed
} = Ember;

export default Component.extend({

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
    if (!this.get('isReadOnly') && !this.get('isDisabled')) {
      if (this.$('input').is(':checked')) {
        run.next(this, function() {
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

  isSelected: computed('model', 'value', function() {
    this.syncInput();
    return this.get('model') === this.get('value');
  })
});
