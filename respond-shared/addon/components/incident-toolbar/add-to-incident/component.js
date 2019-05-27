import layout from './template';
import Component from '@ember/component';
import { debounce } from '@ember/runloop';
import Notifications from 'component-lib/mixins/notifications';
import columns from './columns';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  clearSearchIncidentsResults,
  updateSearchIncidentsText,
  updateSearchIncidentsSortBy,
  selectIncident,
  addEventsToIncident,
  addAlertsToIncident
} from 'respond-shared/actions/creators/add-to-incident-creators';
import {
  getIncidentSearchStatus,
  getIncidentSearchResults,
  getSelectedIncident,
  getIncidentSearchSortBy,
  getIncidentSearchSortIsDescending,
  getIsIncidentNotSelected,
  hasSearchQuery
} from 'respond-shared/selectors/add-to-incident/selectors';

const stateToComputed = (state) => ({
  sortBy: getIncidentSearchSortBy(state),
  isSortDescending: getIncidentSearchSortIsDescending(state),
  incidentSearchStatus: getIncidentSearchStatus(state),
  incidentSearchResults: getIncidentSearchResults(state),
  selectedIncident: getSelectedIncident(state),
  hasSearchQuery: hasSearchQuery(state),
  isIncidentNotSelected: getIsIncidentNotSelected(state)
});

const dispatchToActions = (dispatch) => {
  return {
    searchIncident(value) {
      return dispatch(updateSearchIncidentsText(value));
    },
    clearResults() {
      dispatch(clearSearchIncidentsResults());
    },
    sortIncident(column, isCurrentSortDescending) {
      const currentSortBy = this.get('sortBy');
      // If we're resorting the current column again, flip the sort order, otherwise default to descending in
      // order to make the sort order for a new column predictable.
      const isNewSortDescending = currentSortBy === column.field ? !isCurrentSortDescending : true;
      dispatch(updateSearchIncidentsSortBy(column.field, isNewSortDescending));
    },
    selectIncident(incident) {
      dispatch(selectIncident(incident));
    },
    addtoIncident(data, callbacks) {
      if (this.get('selectedEventIds')) {
        dispatch(addEventsToIncident(data, callbacks));
      } else {
        const { selectedAlerts } = this.getProperties('selectedAlerts');
        dispatch(addAlertsToIncident(selectedAlerts, data, callbacks));
      }
    }
  };
};

/**
 * @class AddToIncident
 * The form (with validation) for adding one or more alerts to an incident
 *
 * @public
 */
const addToIncidentButton = Component.extend(Notifications, {
  layout,
  i18n: service(),

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
   * Represents the alert name/summary that will be set on all the alerts created from the selected events (internally)
   * @property alertName
   * @type {string}
   * @public
   */
  alertName: null,


  /**
   * Represents the alert Severity that will be set on all the alerts created from the selected events (internally)
   * @property alertSeverity
   * @type {number}
   * @public
   */
  alertSeverity: 50,

  /**
   * Represents the selected events that has to go into an incident, passed from investigation-events
   * @property selectedEvents
   * @type {[]}
   * @public
   */
  selectedEventIds: null,

  /**
   * Represents the selected serviceId (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property selectedEndpointId
   * @type {string}
   * @public
   */
  endpointId: null,

  /**
   * Represents the query start Time (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property startTime
   * @type {long}
   * @public
   */
  startTime: null,

  /**
   * Represents the query end Time (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property endTime
   * @type {long}
   * @public
   */
  endTime: null,

  /**
   * Represents the selected alerts that has to go into an incident, passed from respond
   * @property selectedAlerts
   * @type {[]}
   * @public
   */
  selectedAlerts: null,

  /**
   * Debounceable search function that delegates to the `search` action
   * @param value
   * @private
   */
  _search(value) {
    this.send('searchIncident', value);
  },

  didInsertElement() {
    this._super(...arguments);
    this.set('alertName', this.get('i18n').t('respond.alerts.defaultAlertSummaryText').string);
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
      this.finish();
    },
    /**
     * Handler for finalizing the Add Alerts to Incidents transaction and submitting the information to the server
     * @method handleSubmit
     * @private
     */
    handleSubmit() {
      const { selectedIncident } = this.getProperties('selectedIncident');
      let data = selectedIncident.id;
      const selectedEventIds = this.get('selectedEventIds');

      if (selectedEventIds) {
        const { endpointId, startTime, endTime, alertName, alertSeverity } =
            this.getProperties('endpointId', 'startTime', 'endTime', 'alertName', 'alertSeverity');
        data = {
          eventIds: selectedEventIds,
          endpointId,
          range: {
            from: startTime,
            to: endTime
          },
          alertSeverity,
          alertName,
          id: selectedIncident.id
        };
      }

      this.set('isAddToIncidentInProgress', true);
      this.send('addtoIncident', data, {
        // Close the modal and show success notification to the user, if the add-to-incident call has succeeded
        onSuccess: () => {
          this.finish();
          this.send('success', 'respond.incidents.actions.actionMessages.addAlertToIncidentSucceeded',
            { incidentId: selectedIncident.id, entity: this.get('selectedEventIds') ? 'events' : 'alerts' });
          this.set('isAddToIncidentInProgress', false);
        },
        // Show a failure notification if the add-to-incident call has failed
        onFailure: () => {
          this.send('failure', 'respond.incidents.actions.actionMessages.addAlertToIncidentFailed',
            { entity: this.get('selectedEventIds') ? 'events' : 'alerts' });
          this.set('isAddToIncidentInProgress', false);
        }
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(addToIncidentButton);