import Ember from 'ember';
import layout from '../templates/components/rsa-form-select';

const {
  Component,
  inject: {
    service
  },
  computed,
  computed: {
    or
  },
  observer,
  run,
  $
} = Ember;

export default Component.extend({

  layout,

  eventBus: service(),

  label: null,

  prompt: null,

  values: null,

  classNames: ['rsa-form-select'],

  classNameBindings: ['optionsCollapsed',
                      'isReadOnly',
                      'isDisabled',
                      'isError',
                      'isInline',
                      'isSmall',
                      'hasMultipleValues:has-multiple-values:has-single-value'],

  isReadOnly: false,

  isDisabled: false,

  isError: false,

  isSuccess: false,

  isInline: false,

  isSmall: false,

  resolvedDisabled: or('isDisabled', 'isReadOnly'),

  optionsCollapsed: true,

  humanReadableValues: null,

  optionCount: 0,

  multiple: true,

  onchange: null,

  alwaysUsePrompt: false,

  /*
   * Use the prompt if nothing has been selected, or if instructed to always use the
   * prompt. Always using the prompt allows the implementor to control
   * the prompt text.
   */
  usePrompt: computed('values', 'alwaysUsePrompt', function() {
    const values = this.get('values');
    if (!values || values.length === 0 || this.get('alwaysUsePrompt')) {
      return true;
    }
    return false;
  }),

  optionMaxVisible: computed('optionCount', function() {
    if (this.get('optionCount') <= 5) {
      return this.get('optionCount');
    } else {
      return 5;
    }
  }),

  didInsertElement() {
    this.decorateSelectOptions();
    this.updateSelectOptions();

    let _this = this;
    this.get('eventBus').on('rsa-application-click', function(targetEl) {
      if (_this.$()) {
        if (!_this.get('optionsCollapsed') && !_this.$().is(targetEl)) {
          _this.collapseOptions();
        }
      }
    });
  },

  // TODO: remove observer
  valuesDidChange: observer('values.[]', function() {
    run.once(this, function() {
      this.updateSelectOptions();
    });
  }),

  decorateSelectOptions() {
    run.schedule('afterRender', () => {
      let options = this.$('option');
      if (options) {
        this.set('optionCount', options.length);

        options.each(function(i, optionEl) {
          let option = $(optionEl);
          let text = option.text();
          option.attr('data-text', text);
        });
      }
    });
  },

  updateSelectOptions() {
    let that = this;

    run.schedule('afterRender', function() {
      if (that.get('values.length') > 0) {
        if (that.$('select')) {
          that.$('select').val(that.get('values'));
        }

        let optionObjects = [];
        that.get('values').forEach(function(value) {
          let option = that.$(`option[value="${value}"]`);
          if (option) {
            optionObjects.addObject({
              value: option.attr('value'),
              label: option.attr('data-text')
            });
          }
        });
        that.set('humanReadableValues', optionObjects);
      }
    });
  },

  hasMultipleValues: computed('values.length', function() {
    return this.get('values.length') > 1;
  }),

  collapseOptions() {
    this.set('optionsCollapsed', true);
  },

  expandOptions() {
    this.set('optionsCollapsed', false);
  },

  change() {
    const newValue = this.get('multiple') ? this.$('select').val() : [ this.$('select').val() ];
    this.set('values', newValue);
    if (this.get('onchange')) {
      this.sendAction('onchange', newValue);
    }
    this.decorateSelectOptions();
    this.updateSelectOptions();

    if (this.get('values.length') === 1) {
      this.collapseOptions();
    }
  },

  focusOut() {
    this.collapseOptions();
  },

  click(event) {
    if (!this.get('resolvedDisabled')) {
      let that = this;

      run.schedule('afterRender', function() {
        that.$().focus();
      });
    }

    event.stopPropagation();
    this.get('eventBus').trigger('rsa-application-click', event.currentTarget);
  },

  actions: {
    displayOptions() {
      if (!this.get('resolvedDisabled') && this.get('optionsCollapsed')) {
        this.expandOptions();
      }
    },

    collapseOptions() {
      this.collapseOptions();
    },

    removeSelection(value) {
      if (!this.get('resolvedDisabled')) {
        this.$(`option[value="${value}"]`).attr('selected', false);
        this.set('values', this.$('select').val());
      }
    }
  }

});
