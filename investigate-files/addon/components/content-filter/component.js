import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import { listWithoutDefault, appliedFilters } from 'investigate-files/reducers/filter/selectors';
import _ from 'lodash';
import {
  removeFilter,
  resetFilters,
  addFilter,
  createCustomSearch,
  setActiveFilter
} from 'investigate-files/actions/data-creators';


const stateToComputed = (state) => ({
  allFilters: listWithoutDefault(state), // Excluding the default filter from the list
  appliedFilters: appliedFilters(state),
  filterSelected: state.files.filter.filter,
  expressionList: state.files.filter.expressionList
});

const dispatchToActions = {
  setActiveFilter,
  removeFilter,
  resetFilters,
  addFilter,
  createCustomSearch
};

const idRegex = /^[a-z0-9-_ ]+$/i;

const ContentFilter = Component.extend({
  tagName: 'hbox',

  eventBus: service(),

  flashMessage: service(),

  classNames: 'files-content-filter flexi-fit',

  saveFilterName: null,

  @computed('saveFilterName')
  isNameInvalid(name) {
    if (name) {
      return !idRegex.test(name);
    }
  },

  @computed('isNameInvalid', 'isNameEmpty')
  decorator(isNameInvalid, isNameEmpty) {
    const label = isNameInvalid || isNameEmpty ? 'investigateFiles.filter.customFilters.save.errorHeader' :
      'investigateFiles.filter.customFilters.save.header';
    const isError = isNameInvalid || isNameEmpty;
    return { label, isError };
  },

  /**
   * Search the filter control based on user entered text
   * @public
   */
  @computed('allFilters', 'searchTerm')
  filterList(allFilters, searchTerm) {
    const i18n = this.get('i18n');
    let list = [ ...allFilters ]; // Don't want to modify the orignal filter list
    if (searchTerm) {
      list = list.filter((item) => {
        const name = i18n.t(item.label) || '';
        return name.toString().toUpperCase().includes(searchTerm.toUpperCase());
      });
    }
    return _.sortBy(list, [(column) => {
      return i18n.t(column.label).toString();
    }]);
  },

  actions: {

    /**
     * Action to handle filter selection from more filter dropdown. On selection of the checkbox 'addFilter' action will
     * be called with default expression. If checkbox is unchecked 'removeFilter' action will called
     * @param control
     * @public
     */
    onSelection({ selected, panelId, propertyName }) {
      if (!selected) {
        this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-moreOptions');
        this.send('addFilter', { propertyName, propertyValues: null });
        this.send('setActiveFilter', panelId);
      } else {
        run.next(() => {
          this.send('removeFilter', propertyName);
        });
      }
    },
    showSaveFiltersModal() {
      this.get('eventBus').trigger('rsa-application-modal-open-save-search');
    },

    closeSaveFilterModal() {
      this.set('saveFilterName', '');
      this.get('eventBus').trigger('rsa-application-modal-close-save-search');
    },

    saveFilter() {
      const {
        filterSelected,
        saveFilterName,
        expressionList,
        hasError } = this.getProperties('filterSelected', 'saveFilterName', 'expressionList', 'isNameInvalid');
      const filter = {
        name: saveFilterName || '',
        id: filterSelected.id,
        description: filterSelected.description
      };

      if (saveFilterName === '') {
        this.set('isNameEmpty', true);
      }

      if (!saveFilterName) {
        this.set('saveFilterName', '');
        return;
      }

      //  checking if any of the added filter fields are empty.
      if (!expressionList.length || expressionList.some((item) => !item.propertyValues)) {
        this.get('flashMessage').showErrorMessage(this.get('i18n').t('investigateFiles.filter.customFilters.save.filterFieldEmptyMessage'));
        return;
      }
      if (!hasError) {
        this.get('eventBus').trigger('rsa-application-modal-close-save-search');
        const callBackOptions = {
          onSuccess: () => {
            this.get('flashMessage').showFlashMessage('investigateFiles.filter.customFilters.save.success');
          },
          onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
        };
        this.send('createCustomSearch', filter, expressionList, 'FILE', callBackOptions);
      }
      this.set('saveFilterName', '');
    },

    filterNameFocus() {
      this.set('isNameEmpty', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);
