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
  isNone,
  merge,
  run
} = Ember;

export default Component.extend({
  // Default tagName is "li" because this component is most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one incident,
  // the template may want to set tagName to "section").
  classNames: 'rsa-incident-tile',
  classNameBindings: ['isLargeSize:large-size:small-size', 'editModeActive', 'clicked'],
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
  click() {
    if (!this.get('editModeActive')) {
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
    let _this = this;

    this.get('eventBus').on('rsa-application-click', function(targetEl) {
      if (_this.$()) {
        if (_this.get('editModeActive') === true && _this.$().has(targetEl).length === 0) {
          _this.set('editModeActive', false);
          _this.revertIncidentTileSelections();
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
      'selectedStatus': [this.get('incident.statusSort')],
      'pendingStatus': null,
      'selectedPriority': [this.get('incident.prioritySort')],
      'pendingPriority': null,
      'selectedAssignee': [this.get('incident.assignee.id') || '-1'],
      'pendingAssignee': null
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
    let maxLength = isLargeSize ? 63 : 90;

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
   * @description Returns a list of one element with the current status id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  @computed('incident.statusSort')
  selectedStatus: {
    get: (statusSort) => [statusSort],

    set(statusSorts) {
      this.set('pendingStatus', statusSorts.get('firstObject'));
      return statusSorts;
    }
  },

  /**
   * @name selectedPriority
   * @description Returns a list of one element with the current priority id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  @computed('incident.prioritySort')
  selectedPriority: {
    get: (prioritySort) => [prioritySort],

    set(prioritySorts) {
      this.set('pendingPriority', prioritySorts.get('firstObject'));
      return prioritySorts;
    }
  },

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  @computed('incident.assignee.id')
  selectedAssignee: {
    get: (assigneeId) => [assigneeId || -1],

    set(assigneeIds) {
      this.set('pendingAssignee', assigneeIds.get('firstObject'));
      return assigneeIds;
    }
  },

  /**
   * @name statusList
   * @desciption Returns a list of available status.
   * @type number[]
   * @public
   */
  statusList: IncidentConstants.incidentStatusIds,

  /**
   * @name priorityList
   * @description Returns a list of available priorities.
   * @type number[]
   * @public
   */
  priorityList: IncidentConstants.incidentPriorityIds,


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

        let pendingPriority = this.get('pendingPriority');
        let pendingStatus = this.get('pendingStatus');
        let pendingAssignee = this.get('pendingAssignee');

        let attributeChanged = {};

        if (!isNone(pendingPriority)) {
          this.setProperties({
            'incident.prioritySort': parseInt(pendingPriority, 10),
            'incident.priority': IncidentConstants.incidentPriorityString[pendingPriority]
          });
          merge(attributeChanged, {
            priority: IncidentConstants.incidentPriorityString[ pendingPriority],
            prioritySort: parseInt(pendingPriority, 10)
          });
        }
        if (!isNone(pendingStatus)) {
          this.setProperties({
            'incident.statusSort': parseInt(pendingStatus, 10),
            'incident.status': IncidentConstants.incidentStatusString[ pendingStatus ]
          });
          merge(attributeChanged, {
            status: IncidentConstants.incidentStatusString[ pendingStatus ],
            statusSort: parseInt(pendingStatus, 10)
          });
        }
        if (!isNone(pendingAssignee)) {
          if (pendingAssignee === '-1') {
            this.set('incident.assignee', null);
            merge(attributeChanged, { assignee: null });
          } else {
            let updatedAssigneeUser = this.get('users').findBy('id', pendingAssignee);
            let assigneeAttributes = updatedAssigneeUser.getProperties('id', 'firstName', 'lastName', 'email');
            this.set('incident.assignee', assigneeAttributes);
            merge(attributeChanged, { assignee: assigneeAttributes });
          }
        }

        this.sendAction('saveIncidentAction', this.get('incident.id'), attributeChanged);

        this.setProperties({
          'pendingPriority': null,
          'pendingStatus': null,
          'pendingAssignee': null
        });
      }

      event.stopPropagation();
      this.get('eventBus').trigger('rsa-application-click', event.currentTarget);

      return false;
    }
  }
});
