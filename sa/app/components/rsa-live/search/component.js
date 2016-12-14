import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { RESOURCE_TOGGLE_SELECT, FOCUS_RESOURCE, BLUR_RESOURCE } from 'sa/actions/live-content/types';
import computed from 'ember-computed-decorators';
import * as DataActions from 'sa/actions/live-content/live-search-creators';

const {
  Component,
  inject: { service }
} = Ember;

const stateToComputed = ({ live: { selections, search } }) => {

  return {
    selections,
    resourceTypes: search.resourceTypes,
    media: search.media,
    metaKeys: search.metaKeys,
    metaValues: search.metaValues,
    categories: search.categories,
    searchResults: search.results,
    searchCriteria: search.searchCriteria,
    isLoading: search.isLoadingResults,
    focusResource: search.focusResource
  };
};

const dispatchToActions = (dispatch) => {
  return {
    toggleSelect(item) {
      dispatch({ type: RESOURCE_TOGGLE_SELECT, item });
      return false;
    },

    search(criteria) {
      dispatch(DataActions.updateSearchCriteria(criteria));
    },

    sortByColumn(columnId, sortDirection) {
      dispatch(DataActions.updateSearchCriteria({ sort: `${columnId}|${sortDirection}` }));
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
      this.showDetailsPanel();
    }
  };
};

/**
 * Container component that is responsible for orchestrating Live Search page layout and top-level components.
 * Responsible for delegating data down to search-criteria, search-results and search-result-details components,
 * and handling bubbled actions via Ember Redux handlers.
 * @public
 */
const LiveSearch = Component.extend({
  classNames: 'rsa-live-search',
  layoutService: service('layout'),
  eventBus: service(),
  isLoading: false,

  init() {
    this._super(...arguments);
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.panelC', 'hidden');
  },

  getCriteriaPanelState() {
    return this.get('layoutService.panelA');
  },

  @computed('layoutService.panelA')
  isCriteriaPanelCollapsed() {
    return this.getCriteriaPanelState() === 'hidden';
  },

  collapseCriteriaPanel() {
    this.set('layoutService.panelA', 'hidden');
  },

  expandCriteriaPanel() {
    this.set('layoutService.panelA', 'quarter');
  },

  getDetailsPanelState() {
    return this.get('layoutService.panelC');
  },

  isDetailsPanelHidden() {
    return this.getDetailsPanelState() === 'hidden';
  },

  hideDetailsPanel() {
    this.set('layoutService.panelC', 'hidden');
    this.sendAction('onDetailsPanelClosed');
    this.expandCriteriaPanel();
  },

  showDetailsPanel() {
    this.set('layoutService.panelC', 'quarter');
    this.sendAction('onDetailsPanelOpened');
    this.collapseCriteriaPanel();
  }
});

export default connect(stateToComputed, dispatchToActions)(LiveSearch);