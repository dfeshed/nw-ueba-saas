import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  updateGroupCriteria
} from 'admin-source-management/actions/creators/group-wizard-creators';

const dispatchToActions = {
  updateGroupCriteria
};

const AttributeInputs = Component.extend({
  tagName: 'label',
  classNames: ['attributes-inputs'],
  osSelector: ['Windows', 'Linux', 'MacOS'],
  agentSelector: ['Full', 'Insights'],
  actions: {
    handleInputChange(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), [value], 2);
    },
    firstBetweenInput(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 10);
    },
    secondBetweenInput(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 11);
    },
    onChangeOSSelector(value) {
      this.send('updateGroupCriteria', this.get('criteriaPath'), value, 2);
    }
  }
});
export default connect(undefined, dispatchToActions)(AttributeInputs);
