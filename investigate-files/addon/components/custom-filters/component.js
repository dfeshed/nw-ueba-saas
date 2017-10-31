import Component from 'ember-component';
import { connect } from 'ember-redux';

import {
  addSystemFilter,
  setSystemFilterFlag
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  isFilterReset: files.filter.isFilterReset,
  filesFilters: files.filter.fileFilters,
  isSystemFilter: files.filter.isSystemFilter
});
const dispatchToActions = {
  addSystemFilter,
  setSystemFilterFlag
};

const CustomFilterList = Component.extend({
  tagName: 'ul',

  classNames: ['filter-list'],

  activeFilter: null,

  actions: {
    applyCustomFilter(filter) {
      const { criteria: { expressionList } } = filter;
      const filterId = filter.id;
      this.set('activeFilter', filterId);
      this.send('setSystemFilterFlag', false);
      this.send('addSystemFilter', expressionList);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CustomFilterList);