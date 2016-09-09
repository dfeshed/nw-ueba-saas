/**
 * @file JournalEntry Adapter
 * Adapter for the JournalEntry model that extends from the application adapter
 * @public
 */
import ApplicationAdapter from 'sa/application/adapter';

export default ApplicationAdapter.extend({

  /**
   * Overrides the application createdRecord method.
   * builds the socketBody for the create journal-entry API and invokes
   * the app adapter's createRecord method
   * @public
   */
  createRecord(store, type, snapshot) {
    let socketBody = {
      incidentId: snapshot.attr('incidentId'),
      journalMap: snapshot.attr('journalMap')
    };
    return this._super(store, type, snapshot, socketBody);
  }
});
