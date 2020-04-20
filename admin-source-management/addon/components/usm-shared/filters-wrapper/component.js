import Component from '@ember/component';
// import { isEmpty } from '@ember/utils';
// import { success, failure } from 'admin-source-management/utils/flash-messages';
import { filters } from 'admin-source-management/reducers/usm/filters/filters-selectors';
import { parseFilters } from './filters-wrapper-util';
import layout from './template';

export default Component.extend({

  layout,

  classNames: ['filter-wrapper'],

  filterState: null,

  filterType: null,

  filterTypes: null,

  applyFilters: null,

  resetFilters: null,

  didReceiveAttrs() {
    this._super(...arguments);
    const state = {
      filterState: this.get('filterState'),
      filterTypes: this.get('filterTypes')
    };
    this.setProperties({
      allFilters: filters(state)
    });
  },

  actions: {

    filterChanged(filters, reset) {
      const filterType = this.get('filterType');
      if (reset) {
        this.applyFilters(filters, filterType);
        this.resetFilters(filterType);
      } else {
        const expressionList = parseFilters(filters);
        this.set('expressionList', expressionList);
        this.applyFilters(expressionList, filterType);
      }
    },

    resetAllFilters() {
      const resetFilters = this.get('resetFilters');
      resetFilters(...arguments);
    }
  }

});
