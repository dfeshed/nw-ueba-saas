import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { setFilterText, setHighlightedIndex } from 'rsa-list-manager/actions/creators/creators';
import { filterText, filterPlaceholder } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  filterText: filterText(state, attrs.listLocation),
  filterPlaceholder: filterPlaceholder(state, attrs.listLocation)
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
