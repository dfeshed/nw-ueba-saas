import Ember from 'ember';
import IncidentConstants from 'sa/incident/constants';
import IncidentHelper from 'sa/incident/helpers';

const {
  Component,
  computed,
  computed: {
    equal
  },
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
  incidentIsClosed: equal('model.statusSort', IncidentConstants.incStatus.CLOSED),

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle: computed('model.riskScore', function() {
    let riskScore = this.get('model.riskScore');
    return IncidentHelper.riskScoreToBadgeLevel(riskScore);
  }),

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
  incidentSources: computed('model.sources', function() {
    let sources = this.get('model.sources');
    if (sources) {
      return sources.map((source) => IncidentHelper.sourceShortName(source));
    }
  }),

  /**
   * @name selectedStatus
   * @description Returns a list of one element with the current status id. This is consumed by rsa-form-select
   * @public
   */
  selectedStatus: computed('model.statusSort', {
    get() {
      return [this.get('model.statusSort')];
    },

    set(key, value) {
      Logger.log(`Status changed detected: ${ value.get('firstObject') }`);
      run.once(() => {
        let statusVal = parseInt(value.get('firstObject'), 10);
        this.setProperties({
          'model.statusSort': statusVal,
          'model.status': IncidentConstants.incidentStatusString[ statusVal ]
        });
        this._saveIncident();
      });
      return value;
    }
  }),

  /**
   * @name selectedPriority
   * @description Returns a list of one element with the current priority id. This is consumed by rsa-form-select
   * @public
   */
  selectedPriority: computed('model.prioritySort', {
    get() {
      return [this.get('model.prioritySort')];
    },

    set(key, value) {
      Logger.log(`Priority change detected: ${ value.get('firstObject') }`);

      run.once(() => {
        let priorityVal = parseInt(value.get('firstObject'), 10);
        this.setProperties({
          'model.prioritySort': priorityVal,
          'model.priority': IncidentConstants.incidentPriorityString[ priorityVal ]
        });
        this._saveIncident();
      });
      return value;
    }
  }),

  /**
   * @name selectedAssignee
   * @description Returns a list of one element with the current assignee id. This is consumed by rsa-form-select
   * @public
   */
  selectedAssignee: computed('model.assignee.id', {
    get() {
      return [this.get('model.assignee.id') || -1];
    },
    set(key, value) {
      Logger.log(`Assignee change detected: ${ value.get('firstObject') }`);
      run.once(() => {
        // Incident has no assignee
        if (value.get('firstObject') === '-1') {
          this.set('model.assignee', undefined);
        } else {
          if (isEmpty(this.get('model.assignee'))) {
            this.set('model.assignee', {});
          }
          this.set('model.assignee.id', parseInt(value.get('firstObject'), 10));
        }
        this._saveIncident();
      });
      return value;
    }
  }),

  /**
   * @name nameDidChange
   * @description Detects when the name has changed and saves its value into the model
   * @public
   */
  incidentName: computed('model.name', {
    get() {
      return this.get('model.name');
    },
    set(key, value) {
      Logger.log(`Name changed: ${ value }`);
      this.set('model.name', value);
      return value;
    }
  }),

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
