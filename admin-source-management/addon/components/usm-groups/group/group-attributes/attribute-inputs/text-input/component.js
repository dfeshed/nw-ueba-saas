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

const TextInput = Component.extend({
  tagName: 'span',
  classNames: ['text-input'],

  @computed('value')
  localValue(value) {
    return value;
  },

  @computed('value', 'validation', 'stepShowErrors')
  validator(value, validation, stepShowErrors) {
    return groupExpressionValidator(value, validation, true, stepShowErrors);
  },

  actions: {
    handleInputChange(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), [value.trim()], 2);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(TextInput);
