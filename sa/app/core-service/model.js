import Model from 'ember-data/model';
import attr from 'ember-data/attr';

/**
 * Represents a single Core service (e.g., Concentrator, Broker, etc).
 * A list of the available Core services can be fetched from the server for use in investigations, admin, etc.
 * @public
 */
export default Model.extend({
  name: attr(),

  /**
   * Type of Core service; e.g.,"BROKER", "CONCENTRATOR", etc.
   * @type {string}
   * @public
   */
  type: attr()
});
