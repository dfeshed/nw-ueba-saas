import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  groupAttributesMap
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  updateGroupCriteria
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupAttributesMap: groupAttributesMap(state)
});

const dispatchToActions = {
  updateGroupCriteria
};

const GroupAttributes = Component.extend({
  classNames: ['group-attributes'],
  actions: {
    handleAttributeChange(criteriaPath, attr) {
      this.send('updateGroupCriteria', criteriaPath, attr, 0);
    },
    handleOperatorChange(criteriaPath, oprt) {
      this.send('updateGroupCriteria', criteriaPath, oprt[0], 1);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(GroupAttributes);
