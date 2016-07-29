import Ember from 'ember';
import computed, { equal } from 'ember-computed-decorators';
import IncidentConstants from 'sa/incident/constants';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  Logger,
  isEmpty,
  run
} = Ember;

export default Component.extend({

  model: null,
  classNames: 'rsa-incident-detail-header',

  /**
   * @name incidentIsClosed
   * @description returns true if the incident's status is closed
   * @public
   */
  @equal('model.statusSort', IncidentConstants.incStatus.CLOSED) incidentIsClosed: null,

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  @computed('model.riskScore')
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
  @computed('model.sources')
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
  @computed('model.statusSort')
  selectedStatus: {
    get: (statusSort) => [statusSort],

    set(statusSorts) {
      const statusSort = statusSorts.get('firstObject');
      Logger.log(`Status changed detected: ${ statusSort }`);
      run.once(() => {
        let statusVal = parseInt(statusSort, 10);
        this.setProperties({
          'model.statusSort': statusVal,
          'model.status': IncidentConstants.incidentStatusString[ statusVal ]
        });
        this._saveIncident();
      });
      return statusSorts;
    }
  },

  /**
   * @name selectedPriority
   * @description Returns a list of one element with the current priority id. This is consumed by rsa-form-select
   * @public
   */
  @computed('model.prioritySort')
  selectedPriority: {
    get: (prioritySort) => [prioritySort],

    set(prioritySorts) {
      const prioritySort = prioritySorts.get('firstObject');
      Logger.log(`Priority change detected: ${ prioritySort }`);

      run.once(() => {
        let priorityVal = parseInt(prioritySort, 10);
        this.setProperties({
          'model.prioritySort': priorityVal,
          'model.priority': IncidentConstants.incidentPriorityString[ priorityVal ]
        });
        this._saveIncident();
      });
      return prioritySorts;
    }
  },

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @public
   */
  @computed('model.assignee.id')
  selectedAssignee: {
    get: (assigneeId) => [assigneeId || -1],

    set(assigneeIds) {
      const assigneeId = assigneeIds.get('firstObject');
      Logger.log(`Assignee change detected: ${ assigneeId }`);
      run.once(() => {
        // Incident has no assignee
        if (assigneeId === '-1') {
          this.set('model.assignee', undefined);
        } else {
          if (isEmpty(this.get('model.assignee'))) {
            this.set('model.assignee', {});
          }
          this.set('model.assignee.id', parseInt(assigneeId, 10));
        }
        this._saveIncident();
      });
      return assigneeIds;
    }
  },

  /**
   * @name nameDidChange
   * @description Detects when the name has changed and saves its value into the model
   * @public
   */
  @computed('model.name')
  incidentName: {
    get: (name) => name,

    set(name) {
      Logger.log(`Name changed: ${ name }`);
      this.set('model.name', name);
      return name;
    }
  },

  /**
   * @name _saveIncident
   * @description Save the incident model object
   * @private
   */
  _saveIncident() {
    Logger.log('Saving model...');
    this.get('model').save();
  },

  actions: {

    /**
     * @name closeIncidentBtn
     * @description Event handler for the Close Incident button
     * @public
     */
    closeIncidentBtn() {
      run.once(() => {
        Logger.log('Closing incident');
        this.set('selectedStatus', [IncidentConstants.incStatus.CLOSED]);
      });
    },

    /**
     * @name nameLostFocus
     * @description Event handle when name input losses focus and save the new name.
     * @public
     */
    nameLostFocus() {
      Logger.log('nameLostFocus');
      this._saveIncident();
    }
  }
});
