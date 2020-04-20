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

const BetweenTextInput = Component.extend({
  tagName: 'span',
  classNames: ['between-text-input'],
  firstVisited: false,
  firstlockError: false,
  secondVisited: false,
  secondlockError: false,

  firstValue: computed('value', {
    get() {
      if (this._firstValue) {
        return this._firstValue;
      }
      return this.value[0];
    },
    set(key, newValue) {
      this._firstValue = newValue;
      return this._firstValue;
    }
  }),

  secondValue: computed('value', {
    get() {
      if (this._secondValue) {
        return this._secondValue;
      }
      return this.value[1];
    },
    set(key, newValue) {
      this._secondValue = newValue;
      return this._secondValue;
    }
  }),

  firstValueValidator: computed('firstValue', 'validation', 'firstVisited', 'stepShowErrors', function() {
    return groupExpressionValidator(this.firstValue, this.validation, true, (this.firstVisited || this.stepShowErrors));
  }),

  secondValueValidator: computed('secondValue', 'validation', 'secondVisited', 'stepShowErrors', function() {
    return groupExpressionValidator(this.secondValue, this.validation, true, (this.secondVisited || this.stepShowErrors));
  }),

  actions: {
    firstValueFocusIn() {
      const validator = this.get('firstValueValidator');
      if (validator.showError) {
        this.set('firstlockError', true);
      }
      this.set('firstVisited', false);
    },

    firstValueChange(value) {
      this.set('firstVisited', true);
      this.set('firstlockError', false);
      this.set('firstValue', value.trim());
      this.send('updateGroupCriteria', this.get('criteriaPath'), value.trim(), 10);
    },

    secondValueFocusIn() {
      const validator = this.get('secondValueValidator');
      if (validator.showError) {
        this.set('secondlockError', true);
      }
      this.set('secondVisited', false);
    },

    secondValueChange(value) {
      this.set('secondVisited', true);
      this.set('secondlockError', false);
      this.set('secondValue', value.trim());
      this.send('updateGroupCriteria', this.get('criteriaPath'), value.trim(), 11);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(BetweenTextInput);
