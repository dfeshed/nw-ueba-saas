import Ember from 'ember';

const {
  Component
} = Ember;

export default Component.extend({
  classNames: [ 'rsa-incident-overview' ],

  /**
   * Incident summary data fetched from server.
   *
   * Includes top-level incident properties (e.g., id, name, priority, status, created) but not the storyline nor alerts list.
   *
   * @type {object}
   * @public
   */
  info: null,
  actions: {
    /**
     * Handles an update to a metadata property (e.g., priority or status) on the Remediation Task
     * @public
     * @param entityId {string} - The ID of the remediation task to update
     * @param field {string} - The name of the field on the record (e.g., 'priority' or 'status') to update
     * @param updatedValue {*} - The value to be set/updated on the record's field
     */
    update(entityId, field, updatedValue) {
      this.get('updateItem')(entityId, field, updatedValue);
    }
  }
});
