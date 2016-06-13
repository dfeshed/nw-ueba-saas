/**
 * @file Incident List Tile Item component.
 * Represents an Incident as an item in a list (rather than a detailed rendering of an incident's data).
 * @public
 */
import Ember from 'ember';
import { incidentStatusIds, incidentPriorityIds } from 'sa/incident/constants';

export default Ember.Component.extend({
  // Default tagName is "li" because this component is most often displayed in a list format.
  // Templates that use this component can overwrite tagName whenever needed (e.g., if only showing one incident,
  // the template may want to set tagName to "section").
  classNames: 'rsa-incident-tile',
  classNameBindings: ['isLargeSize:large-size:small-size', 'editModeActive'],
  i18n: Ember.inject.service(),
  eventBus: Ember.inject.service('event-bus'),
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
  isIncidentNew: Ember.computed('model.statusSort', function() {
    return (this.get('model.statusSort') === 0);
  }),

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
  isLargeSize: Ember.computed.equal('size', 'large'),
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
  badgeStyle: Ember.computed('model.riskScore', function() {
    let riskScore = this.get('model.riskScore');
    if (riskScore < 30) {
      return 'low';
    } else if (riskScore < 50) {
      return 'medium';
    } else if (riskScore < 70) {
      return 'high';
    } else {
      return 'danger';
    }
  }),

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
        Ember.Logger.log('Updating Incident and calling saveAction action to save it');

        let pendingPriority = this.get('pendingPriority'),
          pendingStatus = this.get('pendingStatus'),
          pendingAssignee = this.get('pendingAssignee');

        if (typeof pendingPriority === 'undefined') {
          pendingPriority = this.get('model.prioritySort');
        }
        if (typeof pendingStatus === 'undefined') {
          pendingStatus = this.get('model.statusSort');
        }
        if (typeof pendingAssignee === 'undefined') {
          pendingAssignee = this.get('model.assignee.id');
        }

        if (Ember.isEmpty(this.get('model.assignee'))) {
          this.set('model.assignee', {});
        }

        this.setProperties({
          'model.statusSort': pendingStatus,
          'model.prioritySort': pendingPriority,
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
  selectedStatus: Ember.computed('model.statusSort',  function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('model.statusSort')];
    }
  }),

  /**
   * @name selectedPriority
   * @description Returns a list of one element with the current priority id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  selectedPriority: Ember.computed('model.prioritySort', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('model.prioritySort')];
    }
  }),

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @type number[]
   * @public
   */
  selectedAssignee: Ember.computed('model.assignee.id', function() {
    if (arguments[1]) {
      return arguments[1];
    } else {
      return [this.get('model.assignee.id') || -1];
    }
  }),

  /**
   * @name selectedStatusDidChange
   * @description Detects when the status has changed and saves it' temporary value into a variable to be used later
   * @public
   */
  selectedStatusDidChange: Ember.observer('selectedStatus.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('pendingStatus', _this.get('selectedStatus.firstObject'));
    });
  }),

  /**
   * @name selectedPriorityDidChange
   * @description Detects when the priority has changed and saves it' temporary value into a variable to be used later
   * @public
   */
  selectedPriorityDidChange: Ember.observer('selectedPriority.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('pendingPriority', _this.get('selectedPriority.firstObject'));
    });
  }),

  /**
   * @name selectedAssigneeDidChange
   * @description Detects when the assignee has changed and saves it' temporary value into a variable to be used later
   * @public
   */
  selectedAssigneeDidChange: Ember.observer('selectedAssignee.firstObject', function() {
    let _this = this;
    Ember.run.once(function() {
      _this.set('pendingAssignee', _this.get('selectedAssignee.firstObject'));
    });
  }),

  /**
   * @name statusList
   * @desciption Returns a list of available status. Each element has an id, text and selected attributes
   * @type number[]
   * @public
   */
  statusList: Ember.computed(function() {
    return incidentStatusIds;
  }),

  /**
   * @name priorityList
   * @description Returns a list of available priorities. Each element has an id, text and selected attributes
   * @type number[]
   * @public
   */
  priorityList: Ember.computed(function() {
    return incidentPriorityIds;
  }),

  /**
   * @assigneeFullName
   * @description Returns Incident' current assignee full name. If the assignee id is not found in the list of users,
   * null is returned instead.
   * @type current assignee First and Last name
   * @public
   */
  assigneeFullName: Ember.computed('model.assignee.id', function() {
    let currentAssigneeId = this.get('model.assignee.id'),
      currentAssignee = null;
    if (currentAssigneeId) {
      currentAssignee = this.get('users').findBy('id', currentAssigneeId);
    }

    if (currentAssignee) {
      // @TODO: Replace firstName with friendlyName once the back-end support is available.
      // See http://bedfordjira.na.rsa.net/browse/ASOC-19171
      return `${ currentAssignee.get('firstName') }`;
    } else {
      return null;
    }
  }),

  /**
   * @name incidentSources
   * @description returns the initials of each source
   * @returns Array
   * @public
   */
  incidentSources: Ember.computed('model.sources', function() {
    let sources = this.get('model.sources');
    if (sources) {
      let res = this.get('model.sources').map(function(source) {
        return source.match(/\b\w/g).join('');
      });
      return res;
    }
  }),

  /**
   * @name contextualTimestamp
   * @description returns the proper timestamp depending upon the state of the incident.
   * @returns Number
   * @public
   */
  contextualTimestamp: Ember.computed('isIncidentNew', 'model.created', 'model.lastUpdated', function() {
    let timestamp = (this.get('isIncidentNew')) ? this.get('model.created') : this.get('model.lastUpdated');
    return timestamp;
  })
});
