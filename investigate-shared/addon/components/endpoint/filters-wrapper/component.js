import Component from '@ember/component';
import { filters, isSystemFilter, selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import layout from './template';
import { isEmpty } from '@ember/utils';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import moment from 'moment';

const DATE_COLUMNS = [
  'agentStatus.lastSeenTime',
  'machine.scanStartTime'
];


const _isFilterHasValues = (filter) => {
  return filter && filter.value && filter.value.length;
};

const _dateValues = (values, unit) => {
  return values.map((item) => {
    return {
      value: item.value || moment.now(),
      relativeValueType: unit,
      relative: !!unit,
      valueType: 'DATE'
    };
  });
};

const parseFilters = (filters) => {
  const expressionList = filters.map((filter) => {
    const { name, operator, value, unit } = filter;
    if (_isFilterHasValues(filter)) {
      let propertyValues = value.map((val) => ({ value: val }));
      if (name === 'size') {
        propertyValues = convertToBytes(unit, propertyValues);
      }
      if (DATE_COLUMNS.includes(name)) {
        propertyValues = _dateValues(propertyValues, unit);
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
      case 'bytes' :
        val = value * 1;
        break;
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

  selectedFilter: null,

  filterState: null,

  filterTypes: null,

  applyFilters: null,

  filterType: null,

  resetFilters: null,

  disableSaveButton: true,

  label: 'investigateFiles.filter.customFilters.save.header',

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

  actions: {

    validate(value) {
      const val = value.trim();
      if (isEmpty(val)) {
        this.set('disableSaveButton', true);
        this.set('isError', false);
      } else {
        if (!idRegex.test(val)) {
          this.set('disableSaveButton', true);
          this.set('isError', true);
          this.set('label', 'investigateFiles.filter.customFilters.save.errorHeader');
        } else {
          this.set('disableSaveButton', false);
          this.set('isError', false);
          this.set('label', 'investigateFiles.filter.customFilters.save.header');
        }
      }
    },

    filterChanged(filters, reset) {
      if (reset) {
        this.applyFilters(filters, this.get('filterType'));
        this.resetFilters(this.get('filterType'));
      } else {
        const expressionList = parseFilters(filters);
        this.set('expressionList', expressionList);
        this.applyFilters(expressionList, this.get('filterType'));
      }
    },

    closeSaveFilterModal() {
      this.set('saveFilterName', '');
      this.set('showSaveFilter', false);
      this.set('disableSaveButton', true);
      this.set('isError', false);
      this.set('label', 'investigateFiles.filter.customFilters.save.header');
    },


    showSaveFilter(filters, saveAs) {
      const expressionList = parseFilters(filters);
      this.set('expressionList', expressionList);
      const { name, id } = this.get('savedFilter') || {};
      if (saveAs || !id || id === 1) {
        this.set('showSaveFilter', true);
        this.set('saveFilterName', '');
      } else {
        const filter = {
          name,
          id
        };
        const callBackOptions = {
          onSuccess: () => {
            success('investigateFiles.filter.customFilters.save.success');
            this.set('showSaveFilter', false);
          },
          onFailure: () => failure('investigateFiles.filter.customFilters.error')
        };
        this.createCustomSearch(filter, expressionList, this.get('filterType'), callBackOptions);
      }
    },

    saveFilter() {

      const { saveFilterName, isNameInvalid, expressionList } = this.getProperties('saveFilterName', 'isNameInvalid', 'expressionList');
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
          onFailure: (response) => {
            if (response.meta.message === 'Access is denied') {
              failure('investigateFiles.filter.customFilters.accessError');
            } else {
              failure('investigateFiles.filter.customFilters.error');
            }
          }
        };
        const filter = {
          name: saveFilterName || ''
        };
        this.createCustomSearch(filter, expressionList, this.get('filterType'), callBackOptions);
      }
      this.set('saveFilterName', '');
      this.set('disableSaveButton', true);
    },

    applyFilters() {
      const applySavedFilters = this.get('applySavedFilters');
      if (applySavedFilters) {
        applySavedFilters(...arguments);
      }
    },

    deleteSavedFilter() {
      const deleteFilter = this.get('deleteFilter');
      if (deleteFilter) {
        deleteFilter(...arguments);
      }
    },

    resetAllFilters() {
      const resetFilters = this.get('resetFilters');
      if (resetFilters) {
        resetFilters(...arguments);
      }
    }
  }
});
