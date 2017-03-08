import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident } }) => ({
  info: incident.info
});

const IncidentOverview = Component.extend({
  classNames: [ 'rsa-incident-overview' ],

  /**
   * Incident summary data fetched from server.
   *
   * Includes top-level incident properties (e.g., id, name, priority, status, created) but not the storyline nor alerts list.
   *
   * @type {object}
   * @public
   */
  info: null
});

export default connect(stateToComputed)(IncidentOverview);