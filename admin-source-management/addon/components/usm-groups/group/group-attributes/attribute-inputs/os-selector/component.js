import Component from '@ember/component';
import { connect } from 'ember-redux';
import { computed } from '@ember/object';
import _ from 'lodash';
import { groupExpressionValidator } from 'admin-source-management/reducers/usm/util/selector-helpers';
import { updateGroupCriteria } from 'admin-source-management/actions/creators/group-wizard-creators';
import { defineGroupStepShowErrors } from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  stepShowErrors: defineGroupStepShowErrors(state)
});

const dispatchToActions = {
  updateGroupCriteria
};

const OsSelector = Component.extend({
  tagName: 'label',
  classNames: ['os-selector'],
  osSelector: null,

  init() {
    this._super(...arguments);
    this.osSelector = ['Windows', 'Linux', 'Mac'];
  },

  // cloneDeep is needed for OS Type power-selector-multiple as it is directly mutating the selected items
  // cloneDeep only data related to this power-selector-multiple.
  selectedValues: computed('value', function() {
    return _.cloneDeep(this.value);
  }),

  validator: computed('selectedValues', 'validation', 'stepShowErrors', function() {
    return groupExpressionValidator(this.selectedValues, this.validation, true, this.stepShowErrors);
  }),

  actions: {
    onChangeOSSelector(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 9);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(OsSelector);
