import Ember from 'ember';

const {
    Component,
    computed,
    inject: { service }
} = Ember;

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

  isCriteriaPanelCollapsed: computed('layoutService.panelA', function() {
    return this.getCriteriaPanelState() === 'hidden';
  }),

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
    toggleSearchCriteriaPanel() {
      if (this.get('isCriteriaPanelCollapsed')) {
        this.expandCriteriaPanel();
        this.hideDetailsPanel();
      } else {
        this.collapseCriteriaPanel();
      }
      return true;
    }
  }
});

export default LiveSearch;
