import Component from '@ember/component';
import { connect } from 'ember-redux';
import { debounce } from '@ember/runloop';
import Notifications from 'respond/mixins/notifications';
import columns from './columns';
import {
  addAlertsToIncident,
  clearSearchIncidentsResults,
  updateSearchIncidentsText,
  updateSearchIncidentsSortBy,
  selectIncident
} from 'respond/actions/creators/add-alerts-to-incident-creators';
import { getSelectedAlerts } from 'respond/selectors/alerts';
import {
  getIncidentSearchStatus,
  getIncidentSearchResults,
  getSelectedIncident,
  getIncidentSearchSortBy,
  getIncidentSearchSortIsDescending,
  getIsAddToAlertsUnavailable,
  hasSearchQuery
} from 'respond/selectors/alert-to-incident';

const stateToComputed = (state) => {
  return {
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
    clearResults() {
      dispatch(clearSearchIncidentsResults());
    },
    addtoIncident(alertIds, incidentId, callbacks) {
      dispatch(addAlertsToIncident(alertIds, incidentId, callbacks));
    },
    sortBy(sortField, isSortDescending) {
      dispatch(updateSearchIncidentsSortBy(sortField, isSortDescending));
    },
    selectIncident(incident) {
      dispatch(selectIncident(incident));
    }
  };
};

/**
 * @class AddToIncident
 * The form (with validation) for adding one or more alerts to an incident
 *
 * @public
 */
const AddToIncident = Component.extend(Notifications, {
  classNames: ['rsa-add-to-incident'],

  /**
   * Columns definition for the incidents search results table
   * @property columns
   * @public true
   */
  columns,

  /**
   * The wait time in milliseconds used to debounce the user's typed search. If a user types another character before
   * the wait time has elapsed, the execution of the debounced search is cancelled.
   * @property searchDelay
   * @type string
   * @public
   */
  searchWait: 500,

  /**
   * Debounceable search function that delegates to the `search` action
   * @param value
   * @private
   */
  _search(value) {
    this.send('search', value);
  },

  actions: {
    /**
     * Handler for each search input keyup event. The handling of the event is debounced to prevent a call to the
     * server for keyup events that occur in quick succession
     * @method handleSearchKeyup
     * @param value
     * @private
     */
    handleSearchKeyup(value = '') {
      debounce(this, this.get('_search'), value, this.get('searchWait'));
    },
    /**
     * Cancels the Add to Incident transaction without making any changes.
     * @method handleCancel
     * @private
     */
    handleCancel() {
      this.sendAction('finish');
    },
    /**
     * Handler for finalizing the Add Alerts to Incidents transaction and submitting the information to the server
     * @method handleSubmit
     * @private
     */
    handleSubmit() {
      const { alertIds, selectedIncident } = this.getProperties('alertIds', 'selectedIncident');

      this.set('isAddAlertsInProgress', true);
      this.send('addtoIncident', alertIds, selectedIncident.id, {
        // Close the modal and show success notification to the user, if the add-to-incident call has succeeded
        onSuccess: () => {
          this.sendAction('finish');
          this.send('success', 'respond.incidents.actions.actionMessages.addAlertToIncidentSucceeded', { incidentId: selectedIncident.id });
          this.set('isAddAlertsInProgress', false);
        },
        // Show a failure notification if the add-to-incident call has failed
        onFailure: () => {
          this.send('failure', 'respond.incidents.actions.actionMessages.addAlertToIncidentFailed');
          this.set('isAddAlertsInProgress', false);
        }
      });
    },
    /**
     * Handler for a sort click on column in the incident search results table
     * @param column
     * @param isCurrentSortDescending
     * @private
     */
    handleResultsSortBy(column, isCurrentSortDescending) {
      const currentSortBy = this.get('sortBy');
      // If we're resorting the current column again, flip the sort order, otherwise default to descending in
      // order to make the sort order for a new column predictable.
      const isNewSortDescending = currentSortBy === column.field ? !isCurrentSortDescending : true;
      this.send('sortBy', column.field, isNewSortDescending);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AddToIncident);
