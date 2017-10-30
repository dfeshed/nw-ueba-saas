import { connect } from 'ember-redux';
import { capitalize } from 'ember-string';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import service from 'ember-service/inject';

import { listWithoutDefault, appliedFilters } from 'investigate-hosts/reducers/filters/selectors';

import {
  removeFilter,
  resetFilters,
  addFilter,
  createCustomSearch
} from 'investigate-hosts/actions/data-creators/filter';


const stateToComputed = (state) => ({
  allFilters: listWithoutDefault(state), // Excluding the default filter from the list
  appliedFilters: appliedFilters(state),
  filterSelected: state.endpoint.filter.filter,
  expressionList: state.endpoint.filter.expressionList
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

  classNames: 'content-filter',

  saveFilterName: null,

  showSaveFilterModal: false,

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
    let style = 'standard';
    let label = 'investigateHosts.hosts.customFilter.save.header';
    let isError = false;
    if (isNameInvalid || isNameEmpty) {
      style = 'error';
      label = 'investigateHosts.hosts.customFilter.save.errorHeader';
      isError = true;
    }
    return { style, isError, label };
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
        const name = this.get('i18n').t(item.label) || '';
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
        this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-hostMoreOptions');
        this.send('addFilter', { propertyName, propertyValues: null });
      } else {
        run.next(() => {
          this.send('removeFilter', propertyName);
        });
      }
    },

    showSaveFiltersModal() {

      this.toggleProperty('showSaveFilterModal');
      run.next(() => {
        this.get('eventBus').trigger('rsa-application-modal-open-save-search');
      });

    },

    closeSaveFilterModal() {
      this.toggleProperty('showSaveFilterModal');
      run.next(() => {
        this.get('eventBus').trigger('rsa-application-modal-close-save-search');
      });
    },

    saveFilter() {
      let isFieldsEmpty = true;
      const {
        saveFilterName,
        expressionList,
        hasError } = this.getProperties('saveFilterName', 'expressionList', 'isNameInvalid');
      const filter = {
        name: saveFilterName,
        description: ''
      };
      for (const schema of expressionList) {
        const { propertyValues } = schema;
        if (propertyValues) {
          isFieldsEmpty = false;
          break;
        }
      }
      if (isFieldsEmpty) {
        this.get('flashMessage').showFlashMessage('investigateHosts.hosts.customFilter.save.filterFieldEmptyMessage');
        return;
      }
      if (!hasError) {
        this.get('eventBus').trigger('rsa-application-modal-close-save-search');
        const callBackOptions = {
          onSuccess: () => {
            this.get('flashMessage').showFlashMessage('investigateHosts.hosts.customFilter.save.success');
          },
          onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
        };
        this.send('createCustomSearch', filter, expressionList, 'MACHINE', callBackOptions);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);
