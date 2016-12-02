import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';
import IncidentConstants from 'sa/incident/constants';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  Object: EmberObject,
  isEmpty,
  get
} = Ember;

export default Component.extend({

  incident: null,
  classNames: 'rsa-incident-detail-header',

  /**
   * @name incidentIsClosed
   * @description returns true if the incident's status is closed
   * @public
   */
  @equal('incident.statusSort', IncidentConstants.incStatus.CLOSED) incidentIsClosed: null,

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  @computed('incident.riskScore')
  badgeStyle: (riskScore) => IncidentHelper.riskScoreToBadgeLevel(riskScore),

  /**
   * @name statusList
   * @desciption Returns a list of available status id.
   * Due to a power-select but, it's needed to convert the list of integer to a list of strings
   * Tracking bug: https://github.com/cibernox/ember-power-select/issues/493
   * @type string[]
   * @public
   */
  statusList: IncidentConstants.incidentStatusIds.map((status) => `${status}`),

  /**
   * @name priorityList
   * @description Returns a list of available priorities id.
   * Due to a power-select but, it's needed to convert the list of integer to a list of strings
   * Tracking bug: https://github.com/cibernox/ember-power-select/issues/493
   * @type string[]
   * @public
   */
  priorityList: IncidentConstants.incidentPriorityIds.map((priority) => `${priority}`),

  /**
   * @name selectedPrioritySort
   * @description Converts the integer prioritySort to a string to match an elements in the `priorityList`
   * Tracking bug: https://github.com/cibernox/ember-power-select/issues/493
   * @type string
   * @public
   */
  @computed('incident.prioritySort')
  selectedPrioritySort: (incidentPriority) => `${incidentPriority}`,

  /**
   * @name selectedStatusSort
   * @description Converts the integer statusSort to a string to match an elements in the `statusList`
   * Tracking bug: https://github.com/cibernox/ember-power-select/issues/493
   * @type string
   * @public
   */
  @computed('incident.statusSort')
  selectedStatusSort: (incidentStatus) => `${incidentStatus}`,

  /**
   * @name selectedAssignee
   * @description returns a matching User based on the incident assignee. If the Incident has not assigne, it returns
   * the `Unassigned` user
   * @type Object
   * @public
   */
  @computed('incident.assignee', 'usersList')
  selectedAssignee: (assignee, usersList) => usersList.findBy('id', isEmpty(assignee) ? -1 : get(assignee, 'id')),

  /**
   * @name incidentSources
   * @description returns the initials of each source
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
   * @name nameDidChange
   * @description Detects when the name has changed and saves its value into the model
   * @public
   */
  @computed('incident.name')
  incidentName: {
    get: (name) => name,

    set(name) {
      this.set('incident.name', name);
      return name;
    }
  },

  @computed('incident.groupBySourceIp')
  incidentSourceIp: IncidentHelper.groupByIp,

  @computed('incident.groupByDestinationIp')
  incidentDestinationIp: IncidentHelper.groupByIp,

  actions: {
    /**
     * @name nameLostFocus
     * @description Event handle when name input losses focus and save the new name.
     * @public
     */
    nameLostFocus() {
      this.sendAction('saveIncidentAction', 'name', this.get('incident.name'));
    },

    /**
     * @description Saves the new status
     * @public
     */
    statusChanged(statusSort) {
      const statusSortVal = parseInt(statusSort, 10);
      this.setProperties({
        'incident.statusSort': statusSortVal,
        'incident.status': IncidentConstants.incidentStatusString[ statusSortVal ]
      });
      const attributeChanged = {
        status: this.get('incident.status'),
        statusSort: this.get('incident.statusSort')
      };
      this.sendAction('saveIncidentAction', attributeChanged);
    },

    /**
     * @description Saves the new priority
     * @public
     */
    priorityChanged(prioritySort) {
      const prioritySortVal = parseInt(prioritySort, 10);
      this.setProperties({
        'incident.prioritySort': prioritySortVal,
        'incident.priority': IncidentConstants.incidentPriorityString[ prioritySortVal ]
      });
      const attributeChanged = {
        priority: this.get('incident.priority'),
        prioritySort: this.get('incident.prioritySort')
      };
      this.sendAction('saveIncidentAction', attributeChanged);
    },

    /**
     * @description Updates the incident's assignee
     * @public
     */
    assigneeChanged(assignee) {
      let updatedAssignee = null;
      const assigneeId = get(assignee, 'id');
      if (assigneeId !== -1) {
        updatedAssignee = this.get('users').findBy('id', assigneeId);
      }
      this.set('incident.assignee', updatedAssignee);
      this.sendAction('saveIncidentAction', 'assignee', this.get('incident.assignee'));
    }
  }
});
