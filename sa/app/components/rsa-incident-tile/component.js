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
  isEmpty
} = Ember;

export default Component.extend({
  // Default tagName is "li" because this component is most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one incident,
  // the template may want to set tagName to "section").
  classNames: 'rsa-incident-tile',
  classNameBindings: ['isLargeSize:large-size:small-size', 'editModeActive'],
  eventBus: service('event-bus'),

  /**
   * The incident data record to be rendered.
   * @type Object
   * @public
   */
  model: null,

  /**
   * @description determines whether or not an incident is considered new.
   * @public
   */
  @equal('model.statusSort', 0) isIncidentNew: null,

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
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  @computed('model.riskScore')
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
      this.sendAction('clickAction', this.get('model'));
    }
  },

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
    this.set('selectedStatus', [this.get('model.statusSort')]);
    this.set('pendingStatus', null);

    this.set('selectedPriority', [this.get('model.prioritySort')]);
    this.set('pendingPriority', null);

    this.set('selectedAssignee', [this.get('model.assignee.id') || '-1']);
    this.set('pendingAssignee', null);

  },

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
        Logger.log('Updating Incident and calling saveAction action to save it');

        let pendingPriority = this.get('pendingPriority');
        let pendingStatus = this.get('pendingStatus');
        let pendingAssignee = this.get('pendingAssignee');

        if (typeof pendingPriority === 'undefined') {
          pendingPriority = this.get('model.prioritySort');
        }
        if (typeof pendingStatus === 'undefined') {
          pendingStatus = this.get('model.statusSort');
        }
        if (typeof pendingAssignee === 'undefined') {
          pendingAssignee = this.get('model.assignee.id');
        }

        if (isEmpty(this.get('model.assignee'))) {
          this.set('model.assignee', {});
        }
        this.setProperties({
          'model.status': IncidentConstants.incidentStatusString[ pendingStatus ],
          'model.priority': IncidentConstants.incidentPriorityString[ pendingPriority ],
          'model.assignee.id': pendingAssignee
        });
        this.sendAction('saveAction', this.get('model'));
      }

      event.stopPropagation();
      this.get('eventBus').trigger('rsa-application-click', event.currentTarget);

      return false;
    }
  },

  /**
   * @name selectedStatus
   * @description Returns a list of one element with the current status id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  @computed('model.statusSort')
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
  @computed('model.prioritySort')
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
  @computed('model.assignee.id')
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
  @computed('model.sources')
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
  @computed('isIncidentNew', 'model.created', 'model.lastUpdated')
  contextualTimestamp: (isIncidentNew, created, lastUpdated) => (isIncidentNew) ? created : lastUpdated
});
