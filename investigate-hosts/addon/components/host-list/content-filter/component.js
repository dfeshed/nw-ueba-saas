import { connect } from 'ember-redux';
import { capitalize } from 'ember-string';
import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import _ from 'lodash';

import { listWithoutDefault, appliedFilters } from 'investigate-hosts/reducers/filters/selectors';

import {
  removeFilter,
  resetFilters,
  addFilter,
  createCustomSearch,
  setActiveFilter
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
  createCustomSearch,
  setActiveFilter
};

const idRegex = /^[a-z0-9-_ ]+$/i;

const ContentFilter = Component.extend({

  tagName: 'hbox',

  eventBus: service(),

  flashMessage: service(),

  routing: service('-routing'),

  classNames: 'content-filter',

  saveFilterName: null,

  showSaveFilterModal: false,

  @computed('saveFilterName')
  isNameInvalid(name) {
    if (name) {
      return !idRegex.test(name);
    }
  },

  @computed('isNameInvalid', 'isNameEmpty')
  decorator(isNameInvalid, isNameEmpty) {
    let label = 'investigateHosts.hosts.customFilter.save.header';
    let isError = false;
    if (isNameInvalid || isNameEmpty) {
      label = 'investigateHosts.hosts.customFilter.save.errorHeader';
      isError = true;
    }
    return { isError, label };
  },


  /**
   * Search the filter control based on user entered text
   * @public
   */
  @computed('allFilters', 'searchTerm')
  filterList(allFilters, searchTerm) {
    const i18n = this.get('i18n');
    let list = [ ...allFilters ]; // Don't want to modify the orignal filter list
    if (searchTerm && searchTerm.length > 3) {
      list = list.filter((item) => {
        const name = i18n.t(item.label) || '';
        return capitalize(name.toString()).includes(capitalize(searchTerm));
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
        this.get('eventBus').trigger('rsa-content-tethered-panel-toggle-hostMoreOptions');
        this.send('addFilter', { propertyName, propertyValues: null });
        this.send('setActiveFilter', panelId);
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
      this.set('saveFilterName', '');
      this.toggleProperty('showSaveFilterModal');
      run.next(() => {
        this.get('eventBus').trigger('rsa-application-modal-close-save-search');
      });
    },

    resetFilter() {
      const routName = 'protected.investigate.investigate-hosts.hosts';
      this.get('routing').transitionTo(routName, [], { query: null });
      this.send('resetFilters');
    },

    saveFilter() {
      const {
        saveFilterName,
        expressionList,
        hasError } = this.getProperties('saveFilterName', 'expressionList', 'isNameInvalid');
      const filter = {
        name: saveFilterName || '',
        description: ''
      };

      if (saveFilterName === '') {
        this.set('isNameEmpty', true);
      }

      if (!saveFilterName) {
        this.set('saveFilterName', '');
        return;
      }
      if (!expressionList.length || expressionList.some((item) => !item.propertyValues)) {
        //  checking if any of the added filter fields are empty.
        this.get('flashMessage').showErrorMessage(this.get('i18n').t('investigateHosts.hosts.customFilter.save.filterFieldEmptyMessage'));
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
      this.set('saveFilterName', '');
    },

    filterNameFocus() {
      this.set('isNameEmpty', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ContentFilter);
