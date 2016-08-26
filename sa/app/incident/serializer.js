/**
 * @file Incident Serializer
 * Formats the data sent to and back from backend.
 * @public
 */
import Ember from 'ember';
import ApplicationSerializer from 'sa/application/serializer';

const {
  isNone,
  typeOf
} = Ember;

export default ApplicationSerializer.extend({

  /**
   * @description normalize the backend response for `updateRecord` method. BackEnd is currently returning the number of
   * updated incidents instead of the list of updated incidents. Instead Ember-Data expects the result to contain the
   * updated Object id.
   * Update-Incident API supports single and bulk edit:
   * - For single edit the expected respond must be {id:...}
   * - For buld edit, an array of {id:...} is generated instead.
   *
   * Example of 'updateRecord' BE response:
   * - single update: { code: 0, data: 1, request: { incidentId: INC-XXX } }
   * - bulk update: { code: 0, data: 3, request: { incidentIds: [INC-XXX, INC-YYY, INC-ZZZ] } }
   *
   * All other requestType [findRecord, createRecord, etc] are using Application normalizeResponse method
   *
   * @public
   */
  normalizeResponse(store, primaryModelClass, payload, id, requestType) {

    if (requestType === 'updateRecord' && typeOf(payload.data) === 'number') {
      if (!isNone(payload.request.incidentId)) {
        // single incident update. Using request.incidentId to generate the result format expected by Ember-Data
        payload.data = { id: payload.request.incidentId };
      } else {
        // bulk incident update. Using request.incidentIds array to generate the result
        payload.data = [];
        payload.data.addObjects(payload.request.incidentIds.map((incidentId) => ({ id: incidentId })));
      }
    }

    return this._super(...arguments);
  }
});
