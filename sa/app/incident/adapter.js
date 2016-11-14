/**
 * @file Incident Adapter
 * Adapter for the Incident model that extends from the application adapter
 * @public
 */
import ApplicationAdapter from 'sa/application/adapter';

export default ApplicationAdapter.extend({

  /**
   * Overrides the application updateRecord method.
   * builds the socketBody for the update incident call and invokes
   * the adapter's updateRecord method
   * @public
   */
  updateRecord(store, type, snapshot) {
    // http://emberjs.com/api/data/classes/DS.Snapshot.html#method_changedAttributes
    // get the changed attributes from the model snapshot, get the keys and
    // constructs the socket body
    const changedAttributes = snapshot.changedAttributes();
    const changedAttributesKey = Object.keys(changedAttributes);
    const updates = {};

    // changedAttributes contains an array of [oldValue , newValue]
    // pass the new value to the websocket call
    changedAttributesKey.forEach((key) => {
      updates[key] = changedAttributes[key][1];
    });

    const socketBody = {
      incidentId: snapshot.id,
      incidentIds: null,
      updates
    };
    return this._super(store, type, snapshot, socketBody);
  }
});
