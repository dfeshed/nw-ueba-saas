import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
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

  @computed('value')
  firstValue(value) {
    return value[0];
  },

  @computed('value')
  secondValue(value) {
    return value[1];
  },

  @computed('firstValue', 'validation', 'firstVisited', 'stepShowErrors')
  firstValueValidator(firstValue, validation, firstVisited, stepShowErrors) {
    return groupExpressionValidator(firstValue, validation, true, (firstVisited || stepShowErrors));
  },

  @computed('secondValue', 'validation', 'secondVisited', 'stepShowErrors')
  secondValueValidator(secondValue, validation, secondVisited, stepShowErrors) {
    return groupExpressionValidator(secondValue, validation, true, (secondVisited || stepShowErrors));
  },

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
      this.send('updateGroupCriteria', this.get('criteriaPath'), value.trim(), 11);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(BetweenTextInput);
