import Ember from 'ember';
import computed from 'ember-computed-decorators';
import connect from 'ember-redux/components/connect';
import * as UIStateActions from 'respond/actions/ui-state-creators';

const {
  Component
} = Ember;

const stateToComputed = ({ respond: { incident } }) => {
  return {
    incidentId: incident.id,
    infoStatus: incident.infoStatus,
    storylineStatus: incident.storylineStatus,
    isEntitiesPanelOpen: incident.isEntitiesPanelOpen,
    isEventsPanelOpen: incident.isEventsPanelOpen,
    isJournalPanelOpen: incident.isJournalPanelOpen
  };
};

const dispatchToActions = (dispatch) => ({
  toggleEntitiesPanel: () => dispatch(UIStateActions.toggleEntitiesPanel()),
  toggleEventsPanel: () => dispatch(UIStateActions.toggleEventsPanel()),
  toggleJournalPanel: () => dispatch(UIStateActions.toggleJournalPanel())
});

const IncidentExplorer = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-explorer'],
  classNameBindings: ['isEntitiesPanelOpen', 'isEventsPanelOpen', 'isJournalPanelOpen', 'openPanelsCountClass'],

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
  storylineStatus: null,

  /**
   * If truthy, entities panel is shown containing force-directed layout.
   *
   * @type {boolean}
   * @public
   */
  isEntitiesPanelOpen: false,

  /**
   * If truthy, events panel is shown containing data table.
   *
   * @type {boolean}
   * @public
   */
  isEventsPanelOpen: false,

  /**
   * If truthy, journal panel is shown containing notes.
   *
   * @type {boolean}
   * @public
   */
  isJournalPanelOpen: false,

  /**
   * Configurable action to be invoked when user clicks UI to show/hide the Entities panel.
   *
   * @type {function}
   * @public
   */
  toggleEntitiesPanel: null,

  /**
   * Configurable action to be invoked when user clicks UI to show/hide the Events panel.
   *
   * @type {function}
   * @public
   */
  toggleEventsPanel: null,

  /**
   * Configurable action to be invoked when user clicks UI to show/hide the Journal panel.
   *
   * @type {function}
   * @public
   */
  toggleJournalPanel: null,

  @computed('isEntitiesPanelOpen', 'isEventsPanelOpen', 'isJournalPanelOpen')
  openPanelsCountClass(entitiesOpen, eventsOpen, journalOpen) {
    let count = 0;
    [ entitiesOpen, eventsOpen, journalOpen ].forEach((bool) => {
      if (bool) {
        count++;
      }
    });
    return `open-panels-count-${count}`;
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentExplorer);