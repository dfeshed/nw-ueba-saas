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
  actions: {
    handleInputChange(originalValue, event) {
      const updatedValue = event.target.value;
      if (updatedValue !== originalValue) {
        this.send('updateGroupCriteria', this.get('criteriaPath'), [updatedValue], 2);
      }
    },
    firstBetweenInput(originalValue, event) {
      const updatedValue = event.target.value;
      if (updatedValue !== originalValue) {
        this.send('updateGroupCriteria', this.get('criteriaPath'), updatedValue, 10);
      }
    },
    secondBetweenInput(originalValue, event) {
      const updatedValue = event.target.value;
      if (updatedValue !== originalValue) {
        this.send('updateGroupCriteria', this.get('criteriaPath'), updatedValue, 11);
      }
    }
  }
});
export default connect(undefined, dispatchToActions)(AttributeInputs);
