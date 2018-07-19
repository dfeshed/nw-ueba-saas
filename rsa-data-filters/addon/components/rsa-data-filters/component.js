import Component from '@ember/component';
import layout from './template';
import computed from 'ember-computed-decorators';

/**
 * Component for rendering the filter controls and emits the filter objects based and applied filters
 * @public
 */
export default Component.extend({

  layout,

  classNames: ['rsa-data-filters'],

  config: null,

  onFilterChange: null,

  appliedFilters: [],

  @computed('config')
  updatedConfig(config) {
    const updated = config.map((conf) => {
      const component = `rsa-data-filters.filters.${conf.type}-filter`;
      return { component, filterOptions: conf };
    });
    return updated;
  },

  _setPreAppliedFilterValues(config) {
    const appliedFilters = [];
    config.forEach((conf) => {
      const { filterValue, type, name } = conf;
      if (filterValue) {
        switch (type) {
          case 'text':
            appliedFilters.push({ ...filterValue, name });
            break;
          case 'list':
          case 'dropdown':
            appliedFilters.push({ name, operator: 'IN', value: filterValue });
            break;
          case 'number':
            appliedFilters.push({ ...filterValue, name });
            break;
          case 'range':
            appliedFilters.push({ name, operator: 'BETWEEN', value: filterValue });
            break;
        }
      }
    });
    this.set('appliedFilters', appliedFilters);
  },

  init() {
    this._super(arguments);
    this._setPreAppliedFilterValues(this.get('config'));
  },


  actions: {
    onChange(filter) {
      const appliedFilter = this.get('appliedFilters');
      const onFilterChange = this.get('onFilterChange');
      const isApplied = appliedFilter.findBy('name', filter.name);
      if (isApplied) {
        const rejected = appliedFilter.rejectBy('name', filter.name);
        rejected.push(filter);
        this.set('appliedFilters', rejected);
      } else {
        appliedFilter.push(filter);
        this.set('appliedFilters', appliedFilter);
      }

      if (onFilterChange) {
        onFilterChange(this.get('appliedFilters'));
      }
    }
  }
});
