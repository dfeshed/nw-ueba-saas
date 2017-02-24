import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import { gt, alias } from 'ember-computed-decorators';
import * as DataActions from 'respond/actions/data-creators';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component,
  inject: { service }
} = Ember;

/**
 * Container component that is responsible for orchestrating Respond Incidents layout and top-level components.
 * @public
 */
const Incidents = Component.extend({
  tagName: 'vbox',
  classNames: 'rsa-respond-incidents',
  classNameBindings: ['isFilterPanelOpen:show-more-filters', 'isAltThemeActive:light-theme'],
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

  loadIncidentsData: function() {
    this.get('redux').dispatch(DataActions.getIncidents());
  }.on('init')
});

const stateToComputed = ({ respond: { incidents } }) => {
  return {
    incidents: incidents.incidents,
    incidentsStatus: incidents.incidentsStatus,
    isInSelectMode: incidents.isInSelectMode,
    incidentsTotal: incidents.incidentsTotal,
    incidentsSelected: incidents.incidentsSelected,
    isFilterPanelOpen: incidents.isFilterPanelOpen,
    isAltThemeActive: incidents.isAltThemeActive
  };
};

const dispatchToActions = (dispatch) => {
  return {
    toggleFilterPanel: () => dispatch(UIStateActions.toggleFilterPanel()),
    toggleIsInSelectMode: () => dispatch(UIStateActions.toggleIsInSelectMode()),
    toggleTheme: () => dispatch(UIStateActions.toggleTheme()),

    select(incident) {
      if (this.get('isInSelectMode')) {
        dispatch(UIStateActions.toggleIncidentSelected(incident));
      } else {
        this.sendAction('viewIncidentDetails', incident.id);
      }
    }
  };
};

export default connect(stateToComputed, dispatchToActions)(Incidents);