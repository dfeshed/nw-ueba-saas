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

  clearFormOnReset: true,

  showSaveFilterButton: false,

  disableSaveFilterButton: false,

  showSaveAsFilterButton: true, // default'ing to true for backward compatibility

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
          case 'date':
            if (filterValue.value && filterValue.value.length === 1) {
              preLoadedFilters.push({ ...filterValue, name, operator: 'GREATER_THAN' });
            } else {
              preLoadedFilters.push({ ...filterValue, name, operator: 'BETWEEN' });
            }
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
    this.set('disableSaveFilterButton', preLoadedFilters.length === 0);
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
      this.set('disableSaveFilterButton', newFilters.length === 0);
    },

    saveFilters(saveAs) {
      const onSave = this.get('onSave');
      if (onSave) {
        onSave(this.get('updatedFilters'), saveAs);
      }
    },

    resetFilters() {
      const onFilterChange = this.get('onFilterChange');
      this.set('disableSaveFilterButton', true);
      if (!this.get('clearFormOnReset')) {
        const preLoadedFilters = this.get('preLoadedFilters');
        if (onFilterChange) {
          this.set('updatedFilters', [...preLoadedFilters]);
          onFilterChange(preLoadedFilters);
        }
      } else {
        this.set('updatedFilters', []);
        if (onFilterChange) {
          onFilterChange([], true);
        }
      }
    }
  }
});
