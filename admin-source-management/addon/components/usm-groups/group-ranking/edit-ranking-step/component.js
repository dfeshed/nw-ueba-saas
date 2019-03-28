import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  groupRanking,
  isLoadingGroupRanking,
  selectedGroupRanking,
  selectedSourceType,
  groupRankingPrevListStatus
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  reorderRanking,
  selectGroupRanking,
  previewRanking,
  previewRankingWithFetch,
  fetchRankingView
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRanking: groupRanking(state),
  isLoadingGroupRanking: isLoadingGroupRanking(state),
  selectedGroupRanking: selectedGroupRanking(state),
  selectedSourceType: selectedSourceType(state),
  groupRankingPrevListStatus: groupRankingPrevListStatus(state)
});

const dispatchToActions = {
  reorderRanking,
  selectGroupRanking,
  previewRanking,
  fetchRankingView,
  previewRankingWithFetch
};

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step',
  actions: {
    handleSelectGroupRanking(index, evt) {
      // top rank is index 0, no need to select
      if (index !== 0) {
        evt.currentTarget.focus();
      }
    }
  },
  didRender() {
    this._super(...arguments);
    const groupRankingTable = document.getElementsByClassName('group-ranking-table');
    if (groupRankingTable.length > 0) {
      const selectedGroup = groupRankingTable[0].getElementsByClassName('is-selected');
      if (selectedGroup.length > 0) {
        selectedGroup[0].focus();
      }
    }
  },
  init() {
    this._super(...arguments);
    this.send('fetchRankingView');
  }
});

export default connect(stateToComputed, dispatchToActions)(EditRankingStep);

