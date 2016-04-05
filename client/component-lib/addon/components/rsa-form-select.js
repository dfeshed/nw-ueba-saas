import Ember from 'ember';
import layout from '../templates/components/rsa-form-select';

export default Ember.Component.extend({
  layout,

  label: null,

  prompt: null,

  values: null,

  classNames: ['rsa-form-select'],

  classNameBindings: ['optionsCollapsed',
                      'isReadOnly',
                      'isDisabled',
                      'isError',
                      'hasMultipleValues:has-multiple-values:has-single-value'],

  isReadOnly: false,

  isDisabled: false,

  isError: false,

  isSuccess: false,

  resolvedDisabled: Ember.computed.or('isDisabled', 'isReadOnly'),

  optionsCollapsed: true,

  didInsertElement() {
    this.decorateSelectOptions();
    this.updateSelectOptions();
  },

  valuesDidChange: (function() {
    Ember.run.once(this, function() {
      this.updateSelectOptions();
    });
  }).observes('values'),

  normalizedValues: (function() {
    let values = this.get('values'),
        normalizedValues = null;

    if (!values) {
      normalizedValues = [];
    } else if (values.split) {
      normalizedValues = values.split(',');
    } else {
      normalizedValues = values;
    }

    return normalizedValues;
  }).property('values.[]'),

  decorateSelectOptions() {
    let that = this;

    Ember.run.schedule('afterRender', function() {
      that.$('option').each(function(i, optionEl) {
        let option = Ember.$(optionEl),
            text = option.text();

        option.attr('selected', false).attr('data-text', text);
      });
    });
  },

  updateSelectOptions() {
    let that = this;

    Ember.run.schedule('afterRender', function() {
      that.get('normalizedValues').forEach(function(value) {
        let option = that.$(`option[value="${value}"]`);
        option.attr('selected', true);
      });
    });
  },

  hasMultipleValues: (function() {
    return this.get('values.length') > 1;
  }).property('values.[]'),

  collapseOptions() {
    this.set('optionsCollapsed', true);
  },

  expandOptions() {
    this.set('optionsCollapsed', false);
  },

  change() {
    this.set('values', this.$('select').val());
    this.updateSelectOptions();

    if (this.get('values.length') === 1) {
      this.collapseOptions();
    }
  },

  focusOut() {
    this.collapseOptions();
  },

  click() {
    if (!this.get('resolvedDisabled')) {
      let that = this;

      Ember.run.schedule('afterRender', function() {
        that.$('select:first').focus();
      });
    }
  },

  actions: {
    displayOptions() {
      if (!this.get('resolvedDisabled')) {
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
