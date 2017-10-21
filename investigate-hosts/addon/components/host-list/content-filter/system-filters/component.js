import Component from 'ember-component';
import layout from './template';
import { connect } from 'ember-redux';

import {
  addSystemFilter,
  resetFilters
} from 'investigate-hosts/actions/data-creators/filter';

const stateToComputed = ({ endpoint }) => ({
  isFilterReset: endpoint.filter.isFilterReset,
  systemFilterList: endpoint.filter.filters

});
const dispatchToActions = {
  addSystemFilter,
  resetFilters
};

const FilterList = Component.extend({
  layout,

  tagName: 'ul',

  classNames: ['filter-list'],

  activeFilter: null,

  actions: {
    applyFilter({ id, criteria }) {
      this.set('activeFilter', id);
      let expressionList = [];
      if (criteria) {
        expressionList = criteria.expressionList;
      } else if (id == 'all') {
        this.send('resetFilters');
      }
      this.send('addSystemFilter', expressionList[0]);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FilterList);
