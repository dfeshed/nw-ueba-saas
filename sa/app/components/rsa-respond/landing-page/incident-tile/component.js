/**
 * @file Incident List Tile Item component.
 * Represents an Incident as an item in a list (rather than a detailed rendering of an incident's data).
 * @public
 */
import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';

import IncidentConstants from 'sa/incident/constants';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  inject: {
    service
  },
  Logger,
  merge,
  run,
  $,
  Object: EmberObject,
  isEmpty,
  get
} = Ember;

export default Component.extend({
  // Default tagName is "li" because this component is most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one incident,
  // the template may want to set tagName to "section").
  classNames: 'rsa-incident-tile',
  classNameBindings: ['isLargeSize:large-size:small-size', 'editModeActive', 'clicked', 'isQueue'],
  eventBus: service(),

  // Property used to control border style state (grey when hovered, blue when clicked).
  clicked: false,

  /**
   * The incident data record to be rendered.
   * @type Object
   * @public
   */
  incident: null,

  /**
   * @description determines whether or not an incident is considered new.
   * @public
   */
  @equal('incident.statusSort', 0) isIncidentNew: null,

  /**
   * @description List of users to be displayed in the Assignee dropdown field
   * @type Array
   * @public
   */
  users: null,

  /**
   * @name size
   * @description defines if the tile should display its full content or not.
   * accepted values: small | large
   * @public
   */
  size: 'large',
  /**
   * @name isLargeSize
   * @description A shorthand that indicates if the tile is been displayed with large size.
   * @return boolean
   * @public
   */
  @equal('size', 'large') isLargeSize: null,

  /**
   * @name editModeActive
   * @description Defines when the tile allows user to interact with the content and save the changes;
   * @return boolean
   * @public
   */
  editModeActive: false,

  /**
   * @description Indicates if the name of the incident was cropped or not.
   * @public
   */
  isNameCropped: false,
  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  @computed('incident.riskScore')
  badgeStyle: (riskScore) => IncidentHelper.riskScoreToBadgeLevel(riskScore),

  /**
   * @name click
   * @description Responds to clicks by firing this component's default action (if any), passing along the click event.
   * The default action is typically set externally by whatever template is using this component.
   * @event
   * @public
   */
  click(event) {
    if (!this.get('editModeActive') && !$(event.target).parents('.rsa-content-ip-connections').length) {
      this.toggleProperty('clicked');
      this.sendAction('clickAction', this.get('incident'));
    }
  },

  @equal('mode', 'queue') isQueue: null,

  /**
   * @name didInsertElement
   * @description used to register a click event listener to prevent having more than one tile in edit-mode-active at
   * the same time.
   * @public
   */
  didInsertElement() {
    this._super(...arguments);

    this.get('eventBus').on('rsa-application-click', (targetEl) => {
      if (this.$()) {
        if (this.get('editModeActive') === true && this.$().has(targetEl).length === 0) {
          this.set('editModeActive', false);
          this.revertIncidentTileSelections();
        }
      }
    });
  },

  /**
   * @name revertIncidentTileSelections
   * @description reverts any user modifications by the incident original value.
   * @public
   */
  revertIncidentTileSelections() {
    this.setProperties({
      'selectedStatus': `${ this.get('incident.statusSort') }`,
      'selectedPriority': `${ this.get('incident.prioritySort') }`,
      'selectedAssignee': this.get('currentAssignee')
    });
  },

  /**
   * @description When the text is too large, it crops the name of the incident and adds '...' at the end.
   * @param name
   * @param isLargeSize
   * @public
   */
  @computed('incident.name', 'isLargeSize')
  incidentName(name, isLargeSize) {
    const maxLength = isLargeSize ? 63 : 90;

    if (name && name.length > maxLength) {
      run.once(() => {
        this.set('isNameCropped', true);
      });
      return `${ name.substr(0, maxLength - 3) }...`;
    }
    return name;
  },

  /**
   * @name selectedStatus
   * @description returns the incident status, or any status set by the user.
   * @type string
   * @public
   */
  @computed('incident.statusSort')
  selectedStatus: (incidentStatus) => `${incidentStatus}`,

  /**
   * @name selectedPriority
   * @description returns the priority, or any priority set by the user.
   * @type string
   * @public
   */
  @computed('incident.prioritySort')
  selectedPriority: (prioritySort) => `${ prioritySort }`,

  /**
   * @name usersList
   * @description Creates an array of the user object and adds the selected parameter to each with the value of false.
   * @param {Object} users Current users object from the model
   * @public
   */
  @computed('users.[]')
  usersList(users) {
    const unassignedUser = EmberObject.create({
      'id': -1
    });
    const arrUsers = [ unassignedUser ];

    if (users) {
      arrUsers.addObjects(users);
    }

    return arrUsers;
  },

  /**
   * @name selectedAssignee
   * @description returns the current assignee for the incident, or any assignee set by the user.
   * @type Object
   * @public
   */
  @computed('currentAssignee')
  selectedAssignee: (currentAssignee) => currentAssignee,

  /**
   * @description Returns the current assignee user. If the Incident has not assigne, it returns the `Unassigned` user.
   * @type Object
   * @private
   */
  @computed('incident.assignee', 'usersList')
  currentAssignee: (assignee, usersList) => usersList.findBy('id', isEmpty(assignee) ? -1 : get(assignee, 'id')),

  /**
   * @name statusList
   * @description Returns a list of available status.
   * @type string[]
   * @public
   */
  statusList: IncidentConstants.incidentStatusIds.map((status) => `${status}`),

  /**
   * @name priorityList
   * @description Returns a list of available priorities.
   * @type string[]
   * @public
   */
  priorityList: IncidentConstants.incidentPriorityIds.map((priority) => `${priority}`),


  /**
   * @name incidentSources
   * @description returns the defined short-name of each source
   * @returns Array
   * @public
   */
  @computed('incident.sources')
  incidentSources(sources) {
    if (sources) {
      return sources.map((source) => IncidentHelper.sourceShortName(source));
    }
  },

  /**
   * @name contextualTimestamp
   * @description returns the proper timestamp depending upon the state of the incident.
   * @returns Number
   * @public
   */
  @computed('isIncidentNew', 'incident.created', 'incident.lastUpdated')
  contextualTimestamp: (isIncidentNew, created, lastUpdated) => (isIncidentNew) ? created : lastUpdated,

  actions: {
    /**
     * @name editButtonClick
     * @description Handles edit-button clicked event
     * First time is clicked the Status, Priority and Assignee become enable to edit;
     * If clicked again, it sends an action to save the updated values
     * @returns {boolean}
     * @public
     */
    editButtonClick(event) {

      this.toggleProperty('editModeActive');

      if (!this.get('editModeActive')) {
        Logger.log('Updating Incident and calling saveIncidentAction action to save it');

        let selectedAssignee = this.get('selectedAssignee');
        const selectedPriority = this.get('selectedPriority');
        const selectedPriorityInt = parseInt(selectedPriority, 10);
        const selectedStatus = this.get('selectedStatus');
        const selectedStatusInt = parseInt(selectedStatus, 10);

        const attributeChanged = {};

        // evaluating change of priority
        if (selectedPriorityInt !== this.get('incident.prioritySort')) {
          this.setProperties({
            'incident.prioritySort': selectedPriorityInt,
            'incident.priority': IncidentConstants.incidentPriorityString[selectedPriority]
          });
          merge(attributeChanged, {
            priority: IncidentConstants.incidentPriorityString[ selectedPriorityInt ]
          });
        }

        // evaluating change of status
        if (selectedStatusInt !== this.get('incident.statusSort')) {
          this.setProperties({
            'incident.statusSort': selectedStatusInt,
            'incident.status': IncidentConstants.incidentStatusString[ selectedStatus ]
          });
          merge(attributeChanged, {
            status: IncidentConstants.incidentStatusString[ selectedStatusInt ]
          });
        }

        // evaluating if the assignee has been changed.
        if (get(selectedAssignee, 'id') != this.get('currentAssignee.id')) {
          if (get(selectedAssignee, 'id') === -1) {
            selectedAssignee = null;
          }
          this.set('incident.assignee', selectedAssignee);
          merge(attributeChanged, { assignee: selectedAssignee });
        }

        // submit the save request if there are pending changes
        if (Object.keys(attributeChanged).length !== 0) {
          this.sendAction('saveIncidentAction', this.get('incident.id'), attributeChanged);
        }
      }

      event.stopPropagation();
      this.get('eventBus').trigger('rsa-application-click', event.currentTarget);

      return false;
    }
  }
});
