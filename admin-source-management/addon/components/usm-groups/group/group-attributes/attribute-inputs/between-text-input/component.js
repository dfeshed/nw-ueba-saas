import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { groupExpressionValidator } from 'admin-source-management/reducers/usm/util/selector-helpers';
import { updateGroupCriteria } from 'admin-source-management/actions/creators/group-wizard-creators';

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

  @computed('firstValue', 'validation')
  firstValueValidator(firstValue, validation) {
    return groupExpressionValidator(firstValue, validation, true, true);
  },

  @computed('secondValue', 'validation')
  secondValueValidator(secondValue, validation) {
    return groupExpressionValidator(secondValue, validation, true, true);
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
export default connect(undefined, dispatchToActions)(BetweenTextInput);
