import layout from './template';
import Component from '@ember/component';
import { debounce } from '@ember/runloop';
import Notifications from 'component-lib/mixins/notifications';
import columns from './columns';

/**
 * @class AddToIncident
 * The form (with validation) for adding one or more alerts to an incident
 *
 * @public
 */
export default Component.extend(Notifications, {
  layout,

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
    this.searchIncident(value);
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

      this.set('isAddAlertsInProgress', true);
      this.addtoIncident(selectedIncident.id, {
        // Close the modal and show success notification to the user, if the add-to-incident call has succeeded
        onSuccess: () => {
          this.finish();
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
      this.sortIncident(column.field, isNewSortDescending);
    },

    handleIncidentSelection(incident) {
      this.selectIncident(incident);
    }
  }
});
