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
  prevDraggVal: 0,
  scrollPos: 0,
  actions: {
    handleScroll(evt) {
      if (evt.buttons) { // mouse down and moving while dragging
        const groupRankingTable = document.getElementsByClassName('group-ranking-table');
        let scrollPos = groupRankingTable[0].scrollTop;
        const tableHeight = groupRankingTable[0].offsetHeight;
        const draggVal = evt.clientY - groupRankingTable[0].getBoundingClientRect().top;
        if (this.get('prevDraggVal') - draggVal < 0 && scrollPos + tableHeight - 100 < scrollPos + draggVal) {
          groupRankingTable[0].scrollTop = scrollPos += 300; // scroll up
        } else if (this.get('prevDraggVal') - draggVal > 0 && scrollPos + 100 > scrollPos + draggVal) {
          groupRankingTable[0].scrollTop = scrollPos -= 300; // scroll down
        }
        this.set('prevDraggVal', draggVal);
      }
    },
    handleSelectGroupRanking(evt) {
      evt.currentTarget.focus();
    },
    handleKeyBoard(index, evt) {
      const groupRankingTable = document.getElementsByClassName('group-ranking-table');
      switch (evt.keyCode) {
        case 40:
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', false) : this.send('reorderRanking', 'arrowDown', index);
            groupRankingTable[0].scrollTop += evt.target.offsetHeight;
          } else {
            evt.target.nextSibling.firstChild ? evt.target.nextSibling.focus() : '';
          }
          break;
        case 38:
          if (evt.shiftKey) {
            evt.altKey ? this.send('setTopRanking', true) : this.send('reorderRanking', 'arrowUp', index);
            groupRankingTable[0].scrollTop -= evt.target.offsetHeight;
          } else {
            evt.target.previousSibling.firstChild ? evt.target.previousSibling.focus() : '';
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
  willRender() {
    this._super(...arguments);
    const groupRankingTable = document.getElementsByClassName('group-ranking-table');
    if (groupRankingTable.length > 0) {
      const scrollPos = groupRankingTable[0].scrollTop;
      this.set('scrollPos', scrollPos);
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
      groupRankingTable[0].scrollTop = this.get('scrollPos');
    }
  },
  init() {
    this._super(...arguments);
    this.send('fetchRankingView');
  }
});

export default connect(stateToComputed, dispatchToActions)(EditRankingStep);

