import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  updateGroupCriteria
} from 'admin-source-management/actions/creators/group-wizard-creators';

import _ from 'lodash';

const dispatchToActions = {
  updateGroupCriteria
};

const OsSelector = Component.extend({
  // cloneDeep is needed for OS Type power-selector-multiple as it is directly mutating the selected items
  // cloneDeep only data related to this power-selector-multiple.
  @computed('selectedValues')
  selectedValuesClone(selectedValues) {
    return _.cloneDeep(selectedValues);
  },
  tagName: 'span',
  classNames: ['os-selector'],
  osSelector: ['Windows', 'Linux', 'MacOS'],
  actions: {
    onChangeOSSelector(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 2);
    }
  }
});
export default connect(undefined, dispatchToActions)(OsSelector);
