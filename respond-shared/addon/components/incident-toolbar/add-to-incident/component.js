import layout from './template';
import Component from '@ember/component';
import { debounce } from '@ember/runloop';
import Notifications from 'component-lib/mixins/notifications';
import columns from './columns';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
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
   * @property alertSummary
   * @type {string}
   * @public
   */
  alertSummary: null,


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
   * Represents whether alert severity entered by user is valid or not
   * @property isAlertSeverityInvalid
   * @type {boolean}
   * @public
   */
  isAlertSeverityInvalid: false,

  /**
   * Debounceable search function that delegates to the `search` action
   * @param value
   * @private
   */
  _search(value) {
    this.send('searchIncident', value);
  },

  /**
   * Indicates whether the form is invalid. the form is invalid if incident is not selected or alert severity which user entered is incorrect
   * @property isInvalid
   * @type {boolean}
   * @public
   */
  @computed('isIncidentNotSelected', 'isAlertSeverityInvalid')
  isInvalid(isIncidentNotSelected, isAlertSeverityInvalid) {
    return isIncidentNotSelected || isAlertSeverityInvalid;
  },

  didInsertElement() {
    this._super(...arguments);
    this.set('alertSummary', this.get('i18n').t('respond.alerts.defaultAlertSummaryText'));
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
      this.send('clearResults');
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
        const { endpointId, startTime, endTime, alertSummary, alertSeverity } =
            this.getProperties('endpointId', 'startTime', 'endTime', 'alertSummary', 'alertSeverity');
        data = {
          eventIds: selectedEventIds,
          endpointId,
          range: {
            from: startTime,
            to: endTime
          },
          alertSeverity,
          alertSummary,
          id: selectedIncident.id
        };
      }

      this.set('isAddToIncidentInProgress', true);
      this.send('addtoIncident', data, {
        // Close the modal and show success notification to the user, if the add-to-incident call has succeeded
        onSuccess: () => {
          this.send('clearResults');
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
    },

    alertSeverityChanged(alertSeverity) {
      // Regex to match number between 1 and 100
      const validSeverityRegex = /^[1-9][0-9]?$|^100$/;

      const isAlertSeverityInvalid = !validSeverityRegex.test(alertSeverity);

      this.set('isAlertSeverityInvalid', isAlertSeverityInvalid);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(addToIncidentButton);
