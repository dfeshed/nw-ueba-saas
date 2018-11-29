import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  groupRanking,
  isLoadingGroupRanking,
  selectedGroupRanking,
  selectedSourceType
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  reorderRanking,
  selectGroupRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRanking: groupRanking(state),
  isLoadingGroupRanking: isLoadingGroupRanking(state),
  selectedGroupRanking: selectedGroupRanking(state),
  selectedSourceType: selectedSourceType(state)
});

const dispatchToActions = {
  reorderRanking,
  selectGroupRanking
};

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step',
  actions: {
    handelSelectGroupRanking(index, evt) {
      // top rank is index 0, no need to select
      if (index !== 0) {
        evt.currentTarget.focus();
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(EditRankingStep);

