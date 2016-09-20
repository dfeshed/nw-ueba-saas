import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';
import IncidentConstants from 'sa/incident/constants';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  Logger,
  run
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
   * @type number[]
   * @public
   */
  statusList: IncidentConstants.incidentStatusIds,

  /**
   * @name priorityList
   * @description Returns a list of available priorities id.
   * @type number[]
   * @public
   */
  priorityList: IncidentConstants.incidentPriorityIds,

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
   * @name selectedStatus
   * @description Returns a list of one element with the current status id. This is consumed by rsa-form-select
   * @public
   */
  @computed('incident.statusSort')
  selectedStatus: {
    get: (statusSort) => [statusSort],

    set(statusSorts) {
      const statusSort = statusSorts.get('firstObject');
      Logger.log(`Status changed detected: ${ statusSort }`);
      run.once(() => {
        let statusVal = parseInt(statusSort, 10);
        this.setProperties({
          'incident.statusSort': statusVal,
          'incident.status': IncidentConstants.incidentStatusString[ statusVal ]
        });
        let attributeChanged = {
          status: this.get('incident.status'),
          statusSort: this.get('incident.statusSort')
        };
        this.sendAction('saveIncidentAction', attributeChanged);
      });
      return statusSorts;
    }
  },

  /**
   * @name selectedPriority
   * @description Returns a list of one element with the current priority id. This is consumed by rsa-form-select
   * @public
   */
  @computed('incident.prioritySort')
  selectedPriority: {
    get: (prioritySort) => [prioritySort],

    set(prioritySorts) {
      const prioritySort = prioritySorts.get('firstObject');
      Logger.log(`Priority change detected: ${ prioritySort }`);

      run.once(() => {
        let priorityVal = parseInt(prioritySort, 10);
        this.setProperties({
          'incident.prioritySort': priorityVal,
          'incident.priority': IncidentConstants.incidentPriorityString[ priorityVal ]
        });
        let attributeChanged = {
          priority: this.get('incident.priority'),
          prioritySort: this.get('incident.prioritySort')
        };
        this.sendAction('saveIncidentAction', attributeChanged);
      });
      return prioritySorts;
    }
  },

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @public
   */
  @computed('incident.assignee.id')
  selectedAssignee: {
    get: (assigneeId) => [assigneeId || -1],

    set(assigneeIds) {
      const assigneeId = assigneeIds.get('firstObject');
      Logger.log(`Assignee change detected: ${ assigneeId }`);
      run.once(() => {
        // Incident has no assignee
        if (assigneeId === '-1') {
          this.set('incident.assignee', null);
        } else {
          let updatedAssigneeUser = this.get('users').findBy('id', assigneeId);
          this.set('incident.assignee', updatedAssigneeUser.getProperties('id', 'firstName', 'lastName', 'email'));
        }
        this.sendAction('saveIncidentAction', 'assignee', this.get('incident.assignee'));
      });
      return assigneeIds;
    }
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

  actions: {
    /**
     * @name nameLostFocus
     * @description Event handle when name input losses focus and save the new name.
     * @public
     */
    nameLostFocus() {
      this.sendAction('saveIncidentAction', 'name', this.get('incident.name'));
    }
  }
});
