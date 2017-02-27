import Ember from 'ember';

const { Component } = Ember;

export default Component.extend({
  classNames: [ 'rsa-incident-banner' ],

  /**
   * ID of the incident we are displaying.
   * @type {string}
   * @public
   */
  incidentId: null,

  /**
   * Incident summary data fetched from server.
   *
   * Includes top-level incident properties (e.g., name, priority, status, created) but not the storyline nor alerts list.
   *
   * @type {object}
   * @public
   */
  info: null
});
