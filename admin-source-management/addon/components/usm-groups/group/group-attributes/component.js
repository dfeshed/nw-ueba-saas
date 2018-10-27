import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';
import {
  groupAttributesMap,
  groupCriteria
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  updateGroupCriteria,
  addCriteria,
  removeCriteria,
  updateCriteriaFromCache
} from 'admin-source-management/actions/creators/group-wizard-creators';

// cloneDeep is needed for OS Type power-selector-multiple as it is directly mutating the selected items
const stateToComputed = (state) => ({
  groupAttributesMap: groupAttributesMap(state),
  criterias: groupCriteria(state)
});

const dispatchToActions = {
  updateGroupCriteria,
  addCriteria,
  removeCriteria,
  updateCriteriaFromCache
};

const GroupAttributes = Component.extend({
  classNames: ['group-attributes'],
  criteriaPath: '',
  @computed('criterias')
  maxTenCriteria(criterias) {
    return criterias.length > 9;
  },
  actions: {
    handleAttributeChange(criteriaPath, attr) {
      this.send('updateGroupCriteria', criteriaPath, attr, 0);
    },
    handleOperatorChange(criteriaPath, oprt) {
      this.send('updateGroupCriteria', criteriaPath, oprt[0], 1);
    }
  },
  init() {
    this._super(...arguments);
    next(() => {
      this.send('updateCriteriaFromCache');
    });
  }
});
export default connect(stateToComputed, dispatchToActions)(GroupAttributes);
