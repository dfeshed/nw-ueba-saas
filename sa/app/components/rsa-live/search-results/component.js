import Ember from 'ember';
import columns from './column-config';
import computed, { equal, gt } from 'ember-computed-decorators';
import * as DataActions from 'sa/actions/live-content/live-search-creators';
import { RESOURCE_TOGGLE_SELECT, FOCUS_RESOURCE, BLUR_RESOURCE } from 'sa/actions/live-content/types';
import connect from 'ember-redux/components/connect';

const { Component } = Ember;

const stateToComputed = ({ live: { selections, search } }) => {

  return {
    selections,
    searchResults: search.results,
    isLoading: search.isLoadingResults,
    focusResource: search.focusResource
  };
};

const dispatchToActions = (dispatch) => {
  return {
    toggleSelect(item) {
      dispatch({ type: RESOURCE_TOGGLE_SELECT, payload: item });
      return false;
    },

    search(criteria) {
      dispatch(DataActions.updateSearchCriteria(criteria));
    },

    sortByColumn(column, direction) {
      column.set('isDescending', (direction === 'desc'));
      this.set('currentSort', column.field);
      dispatch(DataActions.updateSearchCriteria({ sort: `${column.field}|${direction}` }));
    },

    goToBeginning() {
      dispatch(DataActions.firstPage());
    },

    goToPrevious() {
      dispatch(DataActions.previousPage());
    },

    goToNext() {
      dispatch(DataActions.nextPage());
    },

    goToEnd() {
      dispatch(DataActions.lastPage());
    },

    toggleSearchCriteriaPanel() {
      if (this.get('isCriteriaPanelCollapsed')) {
        this.expandCriteriaPanel();
        this.hideDetailsPanel();
        dispatch({ type: BLUR_RESOURCE });
      } else {
        this.collapseCriteriaPanel();
      }
      return true;
    },

    viewResourceDetails(model) {
      dispatch({ type: FOCUS_RESOURCE, payload: model });
      this.sendAction('showDetailsPanel');
    }
  };
};

const SearchResults = Component.extend({
  columnsConfig: columns,
  classNames: ['rsa-live-search-results'],

  /**
   * True if all of the search results are in one page
   * @property isSinglePageResult
   * @public
   */
  @equal('searchResults.totalPages', 1) isSinglePageResult: false,

  /**
   * True if there is one or more search results
   * @property hasResults
   * @public
   */
  @gt('searchResults.totalItems', 0) hasResults: false,

  /**
   * The low end of the viewed results range. If the user is viewing results 51-100, this property would
   * produce the value 51.
   * @property resultRangeLow
   * @public
   */
  @computed('searchResults.totalPages', 'searchResults.pageSize', 'searchResults.pageNumber')
  resultRangeLow(totalPages, pageSize, pageNumber) {
    let firstResultNumber = 1;
    if (totalPages > 1) {
      // add one to page number since zero-based index
      firstResultNumber += ((pageNumber + 1) * pageSize - pageSize);
    }
    return firstResultNumber;
  },

  /**
   * The high end of the viewed results range. If the user is viewing results 51-100, this property would
   * produce the value 100.
   * @property resultRangeLow
   * @public
   */
  @computed('searchResults.totalPages', 'searchResults.pageSize', 'searchResults.totalItems', 'searchResults.pageNumber')
  resultRangeHigh(totalPages, pageSize, totalItems, pageNumber) {
    let lastResultNumber = totalItems;
    if (totalPages !== (pageNumber + 1)) {
      lastResultNumber = ((pageNumber + 1) * pageSize); // add one to page number since zero-based index
    }

    return lastResultNumber;
  },

  actions: {
    rowClick(model, index, event) {
      const $eventTarget = this.$(event.target);

      // Do not send the action if the checkbox is being selected
      if (!$eventTarget.is('.rsa-form-checkbox')) {
        this.send('viewResourceDetails', model);
      }
      return false;
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(SearchResults);
