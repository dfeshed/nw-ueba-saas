import { connect } from 'ember-redux';
import Component from '@ember/component';
import { filters } from 'investigate-files/reducers/file-filter/selectors';
import { applyFilters, createCustomSearch } from 'investigate-files/actions/data-creators';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { run } from '@ember/runloop';


const stateToComputed = (state) => ({
  allFilters: filters(state),
  selectedFilter: state.files.filter.selectedFilter
});

const dispatchToActions = {
  applyFilters,
  createCustomSearch
};

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


const ContentFilter = Component.extend({

  tagName: '',

  showSaveFilter: false,

  saveFilterName: null,

  eventBus: service(),

  flashMessage: service(),

  @computed('saveFilterName')
  isNameInvalid(name) {
    if (name) {
      return /^\s*$/.test(name) || !idRegex.test(name);
    }
  },

  @computed('isNameInvalid', 'isNameEmpty')
  decorator(isNameInvalid, isNameEmpty) {
    const label = isNameInvalid || isNameEmpty ? 'investigateFiles.filter.customFilters.save.errorHeader' :
      'investigateFiles.filter.customFilters.save.header';
    const isError = isNameInvalid || isNameEmpty;
    return { label, isError };
  },

  actions: {

    filterChanged(filters) {
      this.send('applyFilters', parseFilters(filters));
    },

    closeSaveFilterModal() {
      this.set('saveFilterName', '');
      this.set('showSaveFilter', false);
      this.get('eventBus').trigger('rsa-application-modal-close-save-search');
    },


    showSaveFilter(filters) {
      const expressionList = parseFilters(filters);
      const { name, id } = this.get('selectedFilter') || {};
      this.set('expressionList', expressionList);

      if (id) {
        const filter = {
          name,
          id
        };
        const callBackOptions = {
          onSuccess: () => {
            this.get('flashMessage').showFlashMessage('investigateFiles.filter.customFilters.save.success');
          },
          onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
        };
        this.send('createCustomSearch', filter, expressionList, 'FILE', callBackOptions);
      } else {
        this.set('showSaveFilter', true);
        run.next(() => {
          this.get('eventBus').trigger('rsa-application-modal-open-save-search');
        });
      }
    },

    saveFilter() {

      const { saveFilterName, isNameInvalid, expressionList } = this.getProperties('saveFilterName', 'isNameInvalid', 'expressionList');

      const filter = {
        name: saveFilterName || ''
      };

      //  checking if any of the added filter fields are empty.
      if (!expressionList.length || expressionList.some((item) => !item.propertyValues)) {
        this.get('flashMessage').showErrorMessage(this.get('i18n').t('investigateFiles.filter.customFilters.save.filterFieldEmptyMessage'));
        return;
      }
      if (!isNameInvalid) {
        const callBackOptions = {
          onSuccess: () => {
            this.get('flashMessage').showFlashMessage('investigateFiles.filter.customFilters.save.success');
            this.get('eventBus').trigger('rsa-application-modal-close-save-search');
            this.set('showSaveFilter', false);
          },
          onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
        };
        this.send('createCustomSearch', filter, expressionList, 'FILE', callBackOptions);
      }
      this.set('saveFilterName', '');
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);
