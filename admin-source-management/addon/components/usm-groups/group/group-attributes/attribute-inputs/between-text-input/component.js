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

  @computed('value')
  firstValue(value) {
    return value[0];
  },

  @computed('value')
  secondValue(value) {
    return value[1];
  },

  @computed('value', 'validation', 'stepShowErrors')
  firstValueValidator(value, validation, stepShowErrors) {
    return groupExpressionValidator(value[0], validation, true, stepShowErrors);
  },

  @computed('value', 'validation', 'stepShowErrors')
  secondValueValidator(value, validation, stepShowErrors) {
    return groupExpressionValidator(value[1], validation, true, stepShowErrors);
  },

  actions: {
    firstValueChange(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value.trim(), 10);
    },

    secondValueChange(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value.trim(), 11);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(BetweenTextInput);
