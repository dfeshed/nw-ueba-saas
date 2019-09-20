import Component from '@ember/component';
import layout from './template';
import { connect } from 'ember-redux';
import { setFilterText, setHighlightedIndex } from 'rsa-list-manager/actions/creators/creators';
import { filterText, filterPlaceholder } from 'rsa-list-manager/selectors/list-manager/selectors';

const stateToComputed = (state, attrs) => ({
  filterText: filterText(state, attrs.stateLocation),
  filterPlaceholder: filterPlaceholder(state, attrs.stateLocation)
});

const dispatchToActions = {
  setFilterText,
  setHighlightedIndex
};

const ListFilter = Component.extend({
  layout,
  classNames: ['list-filter'],
  stateLocation: undefined,

  filterList(value) {
    this.send('setFilterText', value, this.get('stateLocation'));
  },

  actions: {
    handleInput(e) {
      const { value } = e.target;
      this.filterList(value);
    },

    handleFocus() {
      this.send('setHighlightedIndex', -1, this.get('stateLocation'));
    },

    resetFilter() {
      this.send('setFilterText', '', this.get('stateLocation'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ListFilter);
