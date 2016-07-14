/**
 * @file Investigate Query Bar
 * A UI for the user to query the events of a NetWitness Core service.
 * @public
 */
import Ember from 'ember';

const {
  Component,
  Logger
} = Ember;

export default Component.extend({
  classNames: 'rsa-investigate-query-bar',

  /**
   * List of available Core services (brokers, concentrators, etc) for the user to pick from.
   * Each array item is an instance of `core-service` model.
   * @type {object[]}
   * @public
   */
  coreServiceOptions: null,

  /**
   * Configurable callback to be invoked when user submits the query.
   * This function will be invoked with the following arguments:
   * @param {string} serviceId the ID of the core-service to which the query is directed
   * @param {number} startTime start of the time range for the query filter (in UTC seconds)
   * @param {number} endTime end of the time range for the query filter (in UTC seconds)
   * @type {function}
   * @public
   */
  onSubmit: null,

  actions: {
    // Kicks off a query by invoking the configurable `onSubmit` callback.
    // For now, submits hard-coded values to illustrate the workflow.
    // In an upcoming PR we will fill out this component with dynamic content and submit dynamic values.
    submit() {
      Logger.assert(typeof this.get('onSubmit') === 'function',
        'Invalid onSubmit handler for rsa-query-bar. Submit action aborted.');
      this.get('onSubmit')(
        this.get('coreServiceOptions.firstObject.id'),  // hard-code: first available service
        parseInt(+(new Date()) / 1000 - 60 * 60, 10), // hard-code: now minus 1 hour, in seconds
        parseInt(+(new Date()) / 1000) // hard-code: now, in seconds
      );
    }
  }
});
