import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { gt, alias } from 'ember-computed-decorators';
import * as DataActions from 'respond/actions/data-creators';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component,
  inject: { service }
} = Ember;


const stateToComputed = ({ respond: { incidents } }) => {

  return {
    incidents: incidents.incidents,
    incidentsStatus: incidents.incidentsStatus,
    isInSelectMode: incidents.isInSelectMode,
    incidentsTotal: incidents.incidentsTotal,
    incidentsSelected: incidents.incidentsSelected,
    isFilterPanelOpen: incidents.isFilterPanelOpen,
    isAltThemeActive: incidents.isAltThemeActive,
    focusedIncident: incidents.focusedIncident,
    isTransactionUnderway: incidents.isTransactionUnderway
  };
};

const dispatchToActions = (dispatch) => {
  return {
    initializeIncidents: () => {
      dispatch(DataActions.getIncidents());
      dispatch(DataActions.getAllUsers());
    },
    toggleFilterPanel: () => dispatch(UIStateActions.toggleFilterPanel()),
    toggleIsInSelectMode: () => dispatch(UIStateActions.toggleIsInSelectMode()),
    toggleTheme: () => dispatch(UIStateActions.toggleTheme()),

    select(incident) {
      if (this.get('isInSelectMode')) {
        dispatch(UIStateActions.toggleIncidentSelected(incident.id));
      } else {
        dispatch(UIStateActions.toggleFocusIncident(incident));
      }
    }
  };
};

/**
 * Container component that is responsible for orchestrating Respond Incidents layout and top-level components.
 * @public
 */
const Incidents = Component.extend({
  tagName: 'vbox',
  classNames: 'rsa-respond-incidents',
  classNameBindings: ['isFilterPanelOpen:show-more-filters', 'isAltThemeActive:light-theme', 'isTransactionUnderway:transaction-in-progress'],
  redux: service(),
  i18n: service(),

  @alias('incidents.length')
  incidentsCount: null,

  /**
   * True if there is one or more incidents
   * @property hasResults
   * @public
   */
  @gt('incidentsTotal', 0) hasResults: false,

  onInit: function() {
    this.send('initializeIncidents');
  }.on('init')
});

export default connect(stateToComputed, dispatchToActions)(Incidents);