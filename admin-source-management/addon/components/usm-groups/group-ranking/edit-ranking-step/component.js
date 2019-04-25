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
  fetchRankingView,
  setTopRanking
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
  previewRankingWithFetch,
  setTopRanking
};

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step',
  actions: {
    handleSelectGroupRanking(evt) {
      evt.currentTarget.focus();
    },
    handleKeyBoard(index, evt) {
      switch (evt.keyCode) {
        case 40:
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', false) : this.send('reorderRanking', 'arrowDown', index);
          } else {
            evt.target.nextSibling.focus();
          }
          break;
        case 38:
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', true) : this.send('reorderRanking', 'arrowUp', index);
          } else {
            evt.target.previousSibling.focus();
          }
          break;
        case 37:
          this.send('previewRankingWithFetch', index, 'arrowLeft');
          break;
        case 39:
          this.send('previewRankingWithFetch', index, 'arrowRight');
          break;
        default:
          return;
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

