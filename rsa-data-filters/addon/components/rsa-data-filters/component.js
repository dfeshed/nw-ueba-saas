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

  updatedFilters: [],

  clearFormOnReset: false,

  showSaveFilterButton: false,

  disableSaveFilterButton: false,

  isReset: false,

  @computed('config')
  updatedConfig(config) {
    const updated = config.map((conf) => {
      const component = `rsa-data-filters.filters.${conf.type}-filter`;
      return { component, filterOptions: conf };
    });
    return updated;
  },

  _setPreAppliedFilterValues(config) {
    const preLoadedFilters = [];
    config.forEach((conf) => {
      const { filterValue, type, name } = conf;
      if (filterValue) {
        switch (type) {
          case 'text':
            preLoadedFilters.push({ ...filterValue, name });
            break;
          case 'list':
          case 'dropdown':
            preLoadedFilters.push({ name, operator: 'IN', value: filterValue });
            break;
          case 'number':
            preLoadedFilters.push({ ...filterValue, name });
            break;
          case 'range':
            preLoadedFilters.push({ name, operator: 'BETWEEN', value: filterValue });
            break;
        }
      }
    });
    return preLoadedFilters;
  },

  didReceiveAttrs() {
    this._super(arguments);
    const preLoadedFilters = this._setPreAppliedFilterValues(this.get('config'));
    this.set('preLoadedFilters', preLoadedFilters);
    this.set('updatedFilters', [...preLoadedFilters]);
  },


  actions: {

    onChange(filter) {
      let newFilters = this.get('updatedFilters');
      const onFilterChange = this.get('onFilterChange');
      const isApplied = newFilters.findBy('name', filter.name);

      this.set('isReset', false);
      // If preload filters (in case of save filters) or filter is modified then remove the filter from the list
      // And insert the modified filter to the list
      if (isApplied) {
        newFilters = newFilters.rejectBy('name', filter.name);
        newFilters.push(filter);
        this.set('updatedFilters', newFilters);
      } else {
        newFilters.push(filter);
      }

      if (onFilterChange) {
        onFilterChange(newFilters);
      }

      this.set('updatedFilters', newFilters);
    },

    saveFilters() {
      const onSave = this.get('onSave');
      if (onSave) {
        onSave(this.get('updatedFilters'));
      }
    },

    resetFilters() {
      if (!this.get('clearFormOnReset')) {
        const onFilterChange = this.get('onFilterChange');
        const preLoadedFilters = this.get('preLoadedFilters');
        if (onFilterChange) {
          this.set('isReset', true);
          this.set('updatedFilters', [...preLoadedFilters]);
          onFilterChange(preLoadedFilters);
        }
      }
    }
  }
});
