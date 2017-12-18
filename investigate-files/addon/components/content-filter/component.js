import { connect } from 'ember-redux';
import { capitalize } from 'ember-string';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import { listWithoutDefault, appliedFilters } from 'investigate-files/reducers/filter/selectors';
import {
  removeFilter,
  resetFilters,
  addFilter,
  createCustomSearch
} from 'investigate-files/actions/data-creators';


const stateToComputed = (state) => ({
  allFilters: listWithoutDefault(state), // Excluding the default filter from the list
  appliedFilters: appliedFilters(state),
  filterSelected: state.files.filter.filter,
  expressionList: state.files.filter.expressionList
});

const dispatchToActions = {
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

  @computed('saveFilterName')
  isNameEmpty(saveFilterName) {
    if (saveFilterName === '') {
      return true;
    }
  },

  @computed('isNameInvalid', 'isNameEmpty')
  decorator(isNameInvalid, isNameEmpty) {
    const label = isNameInvalid || isNameEmpty ? 'investigateFiles.filter.customFilters.save.errorHeader' :
      'investigateFiles.filter.customFilters.save.header';
    const style = isNameInvalid || isNameEmpty ? 'error' : 'standard';
    const isError = isNameInvalid || isNameEmpty;
    return { label, style, isError };
  },

  /**
   * Search the filter control based on user entered text
   * @public
   */
  @computed('allFilters', 'searchTerm')
  filterList(allFilters, searchTerm) {
    const list = [ ...allFilters ]; // Don't want to modify the orignal filter list
    if (searchTerm && searchTerm.length > 3) {
      return list.filter((item) => {
        const name = this.get('i18n').t(item.get('decorator.label')) || '';
        return capitalize(name.toString()).includes(capitalize(searchTerm));
      });
    } else {
      return list;
    }
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
        this.set('activeButton', panelId);
        this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-moreOptions');
        this.send('addFilter', { propertyName, propertyValues: null });
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

      //  checking if any of the added filter fields are empty.
      if (expressionList.some((item) => !item.propertyValues)) {
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);
