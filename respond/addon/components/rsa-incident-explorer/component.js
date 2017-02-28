import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident } }) => {
  return {
    incidentId: incident.id,
    infoStatus: incident.infoStatus,
    storylineStatus: incident.storylineStatus
  };
};

const IncidentExplorer = Component.extend({
  tagName: 'vbox',
  classNames: 'rsa-incident-explorer',

  /**
   * id of the incident whose data is to be presented.
   * @type {string}
   * @public
   */
  incidentId: null,

  /**
   * Status of the server request for `info`.
   *
   * @type {string}
   * @public
   */
  infoStatus: null,

  /**
   * Status of the server request for `storyline`.
   * Used to determine whether to show the storyline UI component, or a status message.
   *
   * @type {string}
   * @public
   */
  storylineStatus: null
});

export default connect(stateToComputed)(IncidentExplorer);