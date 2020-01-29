import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import {
  groupRanking,
  isLoadingGroupRanking,
  selectedGroupRanking,
  selectedSourceType,
  groupRankingPrevListStatus,
  hasGroupRankingChanged,
  groupRankingSelectedIndex
} from 'admin-source-management/reducers/usm/group-wizard-selectors';

import {
  reorderRanking,
  selectGroupRanking,
  previewRankingWithFetch,
  fetchRankingView,
  setTopRanking,
  resetRanking
} from 'admin-source-management/actions/creators/group-wizard-creators';

const stateToComputed = (state) => ({
  groupRanking: groupRanking(state),
  isLoadingGroupRanking: isLoadingGroupRanking(state),
  selectedGroupRanking: selectedGroupRanking(state),
  selectedSourceType: selectedSourceType(state),
  groupRankingPrevListStatus: groupRankingPrevListStatus(state),
  hasGroupRankingChanged: hasGroupRankingChanged(state),
  groupRankingSelectedIndex: groupRankingSelectedIndex(state)
});

const dispatchToActions = {
  reorderRanking,
  selectGroupRanking,
  fetchRankingView,
  previewRankingWithFetch,
  setTopRanking,
  resetRanking
};

const EditRankingStep = Component.extend({
  tagName: 'hbox',
  classNames: 'edit-ranking-step',

  @computed('selectedGroupRanking', 'groupRankingSelectedIndex')
  hasSelectedGroup(selectedGroupRanking, groupRankingSelectedIndex) {
    return selectedGroupRanking !== null && groupRankingSelectedIndex !== 0;
  },

  init() {
    this._super(...arguments);
    this.send('fetchRankingView');
  },

  actions: {
    handlePreviewRankingWithFetch(item, index, evt) {
      evt.stopImmediatePropagation();
      this.send('selectGroupRanking', item.name);
      this.send('previewRankingWithFetch', index, item.isChecked);
    },

    handleKeyBoard(index, evt) {
      const groupRankingTable = document.getElementsByClassName('group-ranking-table');
      switch (evt.keyCode) {
        case 40:// arrowDown
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', false) : this.send('reorderRanking', 'arrowDown', index);
            groupRankingTable[0].scrollTop += evt.target.offsetHeight;
          } else {
            evt.target.nextSibling.firstChild && evt.target.nextSibling.focus();
          }
          break;
        case 38:// arrowUp
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', true) : this.send('reorderRanking', 'arrowUp', index);
            groupRankingTable[0].scrollTop -= evt.target.offsetHeight;
          } else {
            evt.target.previousSibling.firstChild && evt.target.previousSibling.focus();
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
  }
});

export default connect(stateToComputed, dispatchToActions)(EditRankingStep);