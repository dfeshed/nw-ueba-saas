import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { groupExpressionValidator } from 'admin-source-management/reducers/usm/util/selector-helpers';
import { updateGroupCriteria } from 'admin-source-management/actions/creators/group-wizard-creators';
import { isDefineGroupStepVisited } from 'admin-source-management/reducers/usm/group-wizard-selectors';

const stateToComputed = (state) => ({
  stepVisited: isDefineGroupStepVisited(state)
});

const dispatchToActions = {
  updateGroupCriteria
};

const OsSelector = Component.extend({
  tagName: 'label',
  classNames: ['os-selector'],
  osSelector: ['Windows', 'Linux', 'Mac'],

  // cloneDeep is needed for OS Type power-selector-multiple as it is directly mutating the selected items
  // cloneDeep only data related to this power-selector-multiple.
  @computed('value')
  selectedValues(value) {
    return _.cloneDeep(value);
  },

  @computed('selectedValues', 'validation', 'stepVisited')
  validator(selectedValues, validation, stepVisited) {
    return groupExpressionValidator(selectedValues, validation, true, stepVisited);
  },

  actions: {
    onChangeOSSelector(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 2);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(OsSelector);
