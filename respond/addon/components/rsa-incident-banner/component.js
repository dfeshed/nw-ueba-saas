import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident } }) => {
  return {
    info: incident.info
  };
};

const IncidentBanner = Component.extend({
  classNames: [ 'rsa-incident-banner' ],

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

export default connect(stateToComputed)(IncidentBanner);