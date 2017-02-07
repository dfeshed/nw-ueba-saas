import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { BLUR_RESOURCE } from 'live-content/actions/live-content/types';
import computed from 'ember-computed-decorators';
import * as DataActions from 'live-content/actions/live-content/live-search-creators';

const {
  Component,
  inject: { service }
} = Ember;

const stateToComputed = ({ live: { search } }) => {
  return {
    resourceTypes: search.resourceTypes,
    media: search.media,
    metaKeys: search.metaKeys,
    metaValues: search.metaValues,
    categories: search.categories,
    searchCriteria: search.searchCriteria,
    searchResults: search.results
  };
};

const dispatchToActions = (dispatch) => {
  return {
    search(criteria) {
      dispatch(DataActions.updateSearchCriteria(criteria));
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
  },

  actions: {
    showDetailsPanel() {
      this.showDetailsPanel();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(LiveSearch);