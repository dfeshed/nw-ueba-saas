import Ember from 'ember';
import connect from 'ember-redux/components/connect';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident } }) => {
  return {
    info: incident.info,
    infoStatus: incident.infoStatus,
    storylineStatus: incident.storylineStatus
  };
};

const dispatchToActions = (/* dispatch */) => {
  return {};  /* nothing yet, coming soon! */
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
   * Data model of an incident. Contains top-level incident info (id, name, priority, status, etc) but not alerts list.
   * @type {object}
   * @public
   */
  info: null,

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

export default connect(stateToComputed, dispatchToActions)(IncidentExplorer);