import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setHighlightedIndex } from 'rsa-list-manager/actions/creators/creators';
import { highlightedIndex } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state) => ({
  highlightedIndex: highlightedIndex(state)
});

const dispatchToActions = {
  setHighlightedIndex
};

const ListFilter = Component.extend({
  layout,
  classNames: ['list-filter'],
  listName: null,
  originalList: null,
  filterAction: null,
  updateFilteredList: null,
  filterText: '',

  didInsertElement() {
    this.initializeElement();
  },

  initializeElement() {
    this.set('filterText', '');
    this.get('updateFilteredList')(this.get('originalList'));
    this.send('setHighlightedIndex', -1);
  },

  filterList(value) {
    const originalList = this.get('originalList');

    let filteredList = originalList;

    if (this.get('filterAction')) {
      filteredList = this.get('filterAction')(value);
    } else {
      if (originalList) {
        filteredList = originalList.filter((item) => item.name.toLowerCase().includes(value.toLowerCase()));
      }
    }
    this.get('updateFilteredList')(filteredList);
    this.send('setHighlightedIndex', -1);
  },

  @computed('listName')
  filterPlaceholder(listName) {
    return `Filter ${listName.toLowerCase()}`;
  },

  actions: {
    handleInput(e) {
      const { value } = e.target;
      this.set('filterText', value);
      this.filterList(value);
    },

    handleFocus() {
      this.send('setHighlightedIndex', -1);
    },

    resetFilter() {
      this.initializeElement();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListFilter);
