/**
 * @file Breadcrumb component
 * Displays the constituent pieces of a given Netwitness Core query.
 * @public
 */
import Ember from 'ember';

const { computed, Component } = Ember;

export default Component.extend({
  tagName: 'nav',
  classNames: 'rsa-investigate-breadcrumb',

  /**
   * An object whose properties are the filter parameters for a Netwitness Core query; including
   * `serviceId`, `startTime`, `endTime` and an optional `metaFilter`.
   * @see protected/investigate/state/query
   * @type {object}
   * @public
   */
  query: undefined,

  /**
   * List of known Netwitness Core services.
   * Used for looking up the name of a service by its ID.
   * @type {object[]}
   * @public
   */
  services: undefined,

  // Computes the service object in `services` that matches `query.serviceId`.
  _service: computed('services', 'query.serviceId', function() {
    let services = this.get('services');
    let id = this.get('query.serviceId');
    if (services && id) {
      return services.findBy('id', id);
    }
    return null;
  })
});
