import Component from '@ember/component';
import { gt } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import * as ACTION_TYPES from '../../../actions/types';
import {
  getSelectedAlerts,
  hasSelectedAlertsBelongingToIncidents
} from 'respond/selectors/alerts';
import {
  getGroupedCategories,
  getPriorityTypes
} from 'respond/selectors/dictionaries';
import { getEnabledUsers } from 'respond/selectors/users';
import {
  getIncidentSearchStatus,
  getIncidentSearchResults,
  getSelectedIncident,
  getIncidentSearchSortBy,
  getIncidentSearchSortIsDescending,
  getIsAddToAlertsUnavailable,
  hasSearchQuery
} from 'respond/selectors/alert-to-incident';
import {
  addAlertsToIncident,
  clearSearchIncidentsResults,
  updateSearchIncidentsText,
  updateSearchIncidentsSortBy,
  selectIncident
} from 'respond/actions/creators/add-alerts-to-incident-creators';

const stateToComputed = (state) => {
  return {
    hasSelectedAlertsBelongingToIncidents: hasSelectedAlertsBelongingToIncidents(state),
    priorityTypes: getPriorityTypes(state),
    groupedCategories: getGroupedCategories(state),
    enabledUsers: getEnabledUsers(state),
    alertIds: getSelectedAlerts(state),
    sortBy: getIncidentSearchSortBy(state),
    isSortDescending: getIncidentSearchSortIsDescending(state),
    incidentSearchStatus: getIncidentSearchStatus(state),
    incidentSearchResults: getIncidentSearchResults(state),
    selectedIncident: getSelectedIncident(state),
    hasSearchQuery: hasSearchQuery(state),
    isAddToAlertsUnavailable: getIsAddToAlertsUnavailable(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    search(value) {
      return dispatch(updateSearchIncidentsText(value));
    },
    clearSearchIncidentsResults() {
      dispatch(clearSearchIncidentsResults());
    },
    addtoIncident(incidentId, callbacks) {
      const { alertIds } = this.getProperties('alertIds');
      dispatch(addAlertsToIncident(alertIds, incidentId, callbacks));
    },
    sortBy(sortField, isSortDescending) {
      dispatch(updateSearchIncidentsSortBy(sortField, isSortDescending));
    },
    selectIncident(incident) {
      dispatch(selectIncident(incident));
    },
    create(incidentDetails) {
      const { alertIds } = this.getProperties('alertIds');
      dispatch({ type: ACTION_TYPES.CREATE_INCIDENT_SAGA, incidentDetails, alertIds });
    }
  };
};

/**
 * @class AlertControls
 * Represents the bulk action controls for updating, deleting alerts
 *
 * @public
 */
const AlertControls = Component.extend({
  classNames: ['rsa-alerts-toolbar-controls'],
  accessControl: service(),
  i18n: service(),

  @gt('itemsSelected.length', 1) isBulkSelection: false,

  updateConfirmationDialogId: 'bulk-update-entities',
  deleteConfirmationDialogId: 'delete-entities',

  /**
   * Creates a closure around the the updateItem call so that the function can be passed as a callback or invoked directly
   * @param entityIds
   * @param fieldName
   * @param value
   * @returns {function()}
   * @private
   */
  _update(entityIds, fieldName, value) {
    return () => {
      this.get('updateItem')(entityIds, fieldName, value);
    };
  },

  /**
   * Creates a closure around the deleteItem call so that the function can be passed as a callback or invoked directly
   * @param entityIds
   * @returns {function()}
   * @private
   */
  _delete(entityIds) {
    return () => {
      this.get('deleteItem')(entityIds);
    };
  },

  actions: {
    deleteAlerts() {
      const { itemsSelected, confirm, i18n, deleteConfirmationDialogId } =
        this.getProperties('itemsSelected', 'confirm', 'i18n', 'deleteConfirmationDialogId');
      const deleteWarningTitle = i18n.t('respond.alerts.actions.actionMessages.deleteWarningTitle');
      const deleteWarnings = [
        i18n.t('respond.alerts.actions.actionMessages.removeFromIncidentWarning'),
        i18n.t('respond.alerts.actions.actionMessages.deleteIncidentWarning'),
        i18n.t('respond.alerts.actions.actionMessages.resetAlertNameFiltersWarning')
      ];
      const deleteItems = this._delete(itemsSelected);
      confirm(deleteConfirmationDialogId, {
        count: itemsSelected.length,
        warningTitle: deleteWarningTitle,
        warnings: deleteWarnings
      }, deleteItems);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AlertControls);
