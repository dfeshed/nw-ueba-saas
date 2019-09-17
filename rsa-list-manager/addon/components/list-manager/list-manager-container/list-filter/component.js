import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { setFilterText, setHighlightedIndex } from 'rsa-list-manager/actions/creators/creators';
import { listName, filterText } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  listName: listName(state, attrs.listLocation),
  filterText: filterText(state, attrs.listLocation)
});

const dispatchToActions = {
  setFilterText,
  setHighlightedIndex
};

const ListFilter = Component.extend({
  layout,
  classNames: ['list-filter'],
  listLocation: undefined,

  didInsertElement() {
    this.initializeElement();
  },

  initializeElement() {
    this.send('setFilterText', '', this.get('listLocation'));
  },

  filterList(value) {
    this.send('setFilterText', value, this.get('listLocation'));
  },

  @computed('listName')
  filterPlaceholder(listName) {
    return `Filter ${listName.toLowerCase()}`;
  },

  actions: {
    handleInput(e) {
      const { value } = e.target;
      this.filterList(value);
    },

    handleFocus() {
      this.send('setHighlightedIndex', -1, this.get('listLocation'));
    },

    resetFilter() {
      this.initializeElement();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListFilter);
