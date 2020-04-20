import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
import { groupExpressionValidator } from 'admin-source-management/reducers/usm/util/selector-helpers';
import { updateGroupCriteria } from 'admin-source-management/actions/creators/group-wizard-creators';
import { defineGroupStepShowErrors } from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  stepShowErrors: defineGroupStepShowErrors(state)
});

const dispatchToActions = {
  updateGroupCriteria
};

const TextInput = Component.extend({
  tagName: 'span',
  classNames: ['text-input'],
  visited: false,
  lockError: false,

  localValue: computed('value', {
    get() {
      if (this._localValue) {
        return this._localValue;
      }
      return this.value;
    },
    set(key, newValue) {
      this._localValue = newValue;
      return this._localValue;
    }
  }),

  validator: computed('localValue', 'validation', 'visited', 'stepShowErrors', function() {
    return groupExpressionValidator(this.localValue, this.validation, true, (this.visited || this.stepShowErrors));
  }),

  actions: {
    handleFocusIn() {
      const validator = this.get('validator');
      if (validator.showError) {
        this.set('lockError', true);
      }
      this.set('visited', false);
    },
    handleInputChange(value) {
      this.set('visited', true);
      this.set('lockError', false);
      this.set('localValue', value.trim());
      this.send('updateGroupCriteria', this.get('criteriaPath'), [value.trim()], 2);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TextInput);
