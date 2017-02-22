import Ember from 'ember';
import connect from 'ember-redux/components/connect';
import computed, { gt } from 'ember-computed-decorators';
import * as DataActions from 'respond/actions/data-creators';
import {
  TOGGLE_INCIDENT_SELECTED,
  TOGGLE_SELECT_MODE,
  TOGGLE_THEME,
  TOGGLE_FILTER_PANEL } from 'respond/actions/types';

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

  @computed('incidents')
  incidentCount(incidents) {
    return incidents.length;
  },

  /**
   * True if there is one or more incidents
   * @property hasResults
   * @public
   */
  @gt('incidentsTotal', 0) hasResults: false,

  @computed('incidentsTotal', 'incidentCount', 'i18n')
  showingIncidentsMessage(incidentsTotal, incidentsCount, i18n) {
    return `${i18n.t('respond.incidents.footer.showing')} ${incidentsCount} ${i18n.t('respond.incidents.footer.outOf')}
            ${incidentsTotal} ${i18n.t('respond.incidents.label')}`;
  },

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
    select(incident) {
      if (this.get('isInSelectMode')) {
        dispatch({
          type: TOGGLE_INCIDENT_SELECTED,
          payload: incident
        });
      } else {
        this.sendAction('viewIncidentDetails', incident.id);
      }
    },

    toggleIsInSelectMode() {
      dispatch({
        type: TOGGLE_SELECT_MODE
      });
    },

    toggleFilterPanel() {
      dispatch({
        type: TOGGLE_FILTER_PANEL
      });
    },

    toggleTheme() {
      dispatch({
        type: TOGGLE_THEME
      });
    }
  };
};

export default connect(stateToComputed, dispatchToActions)(Incidents);