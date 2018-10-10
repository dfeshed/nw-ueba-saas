import Component from '@ember/component';
import { filters, isSystemFilter, selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import layout from './template';
import { isEmpty } from '@ember/utils';
import { success, failure } from 'investigate-shared/utils/flash-messages';

const _isFilterHasValues = (filter) => {
  return filter && filter.value && filter.value.length;
};

const parseFilters = (filters) => {
  const expressionList = filters.map((filter) => {
    const { name, operator, value, unit } = filter;
    if (_isFilterHasValues(filter)) {
      let propertyValues = value.map((val) => ({ value: val }));
      if (name === 'size') {
        propertyValues = convertToBytes(unit, propertyValues);
      }

      return {
        restrictionType: operator,
        propertyValues,
        propertyName: name
      };
    }
  }).compact();

  return expressionList;
};

const convertToBytes = (unit, values) => {
  const convertedValue = values.map((item) => {
    const { value } = item;
    let val = value;
    switch (unit) {
      case 'KB' :
        val = value * 1024;
        break;
      case 'MB' :
        val = value * Math.pow(1024, 2);
        break;
      case 'GB' :
        val = value * Math.pow(1024, 3);
        break;
    }
    return { value: val };
  });
  return convertedValue;
};

const idRegex = /^[a-z0-9-_ ]+$/i;


export default Component.extend({

  layout,

  classNames: ['filter-wrapper'],

  showSaveFilter: false,

  saveFilterName: null,

  eventBus: service(),

  selectedFilter: null,

  filterState: null,

  filterTypes: null,

  applyFilters: null,

  filterType: null,

  resetFilters: null,

  didReceiveAttrs() {
    this._super(...arguments);
    const state = {
      filter: this.get('filterState'),
      filterTypes: this.get('filterTypes')
    };

    this.setProperties({
      allFilters: filters(state),
      isSystemFilter: isSystemFilter(state),
      selectedFilterId: selectedFilterId(state),
      savedFilter: savedFilter(state)
    });
  },

  @computed('saveFilterName')
  isNameInvalid(name) {
    if (name) {
      return /^\s*$/.test(name) || !idRegex.test(name);
    }
  },

  @computed('saveFilterName')
  isNameEmpty(name) {
    if (name !== null) {
      return isEmpty(name);
    }
  },

  @computed('isNameInvalid', 'isNameEmpty')
  displayProperties(isNameInvalid, isNameEmpty) {
    const label = isNameInvalid || isNameEmpty ? 'investigateFiles.filter.customFilters.save.errorHeader' :
      'investigateFiles.filter.customFilters.save.header';
    const isError = isNameInvalid || isNameEmpty;
    return { label, isError };
  },

  actions: {

    filterChanged(filters, reset) {
      if (reset) {
        this.applyFilters(filters);
        this.resetFilters();
      } else {
        const expressionList = parseFilters(filters);
        this.set('expressionList', expressionList);
        this.applyFilters(expressionList);
      }
    },

    closeSaveFilterModal() {
      this.set('saveFilterName', '');
      this.set('showSaveFilter', false);
      this.get('eventBus').trigger('rsa-application-modal-close-save-search');
    },


    showSaveFilter(filters) {
      const expressionList = parseFilters(filters);
      this.set('expressionList', expressionList);
      const savedFilter = this.get('savedFilter');

      if (savedFilter) {
        this.set('saveFilterName', savedFilter.name);
      }

      this.set('showSaveFilter', true);
    },

    saveFilter() {

      const { saveFilterName, isNameInvalid, expressionList } = this.getProperties('saveFilterName', 'isNameInvalid', 'expressionList');
      const { name, id } = this.get('savedFilter') || {};
      //  checking if any of the added filter fields are empty.
      if (!expressionList.length || expressionList.some((item) => !item.propertyValues)) {
        failure('investigateFiles.filter.customFilters.save.filterFieldEmptyMessage');
        return;
      }
      if (!isNameInvalid) {
        const callBackOptions = {
          onSuccess: () => {
            success('investigateFiles.filter.customFilters.save.success');
            this.set('showSaveFilter', false);
          },
          onFailure: () => failure('investigateFiles.filter.customFilters.error')
        };
        if (saveFilterName === name) {
          const filter = {
            name,
            id
          };

          this.createCustomSearch(filter, expressionList, this.get('filterType'), callBackOptions);
        } else {
          const filter = {
            name: saveFilterName || ''
          };
          this.createCustomSearch(filter, expressionList, this.get('filterType'), callBackOptions);
        }

      }
      this.set('saveFilterName', '');
    }
  }
});
