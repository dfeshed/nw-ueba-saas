import { computed } from '@ember/object';
import layout from './template';
import Component from '@ember/component';
import Notifications from 'component-lib/mixins/notifications';
import { isEmpty, typeOf } from '@ember/utils';
import {
  getEnabledUsers,
  getGroupedCategories,
  getPriorityTypes
} from '../../../selectors/create-incident/selectors';
import {
  createIncidentFromEvents,
  createIncidentFromAlerts
} from 'respond-shared/actions/creators/create-incident-creators';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    groupedCategories: getGroupedCategories(state),
    enabledUsers: getEnabledUsers(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    createIncident(incidentDetails, callbacks) {
      if (this.get('selectedEventIds')) {
        dispatch(createIncidentFromEvents(incidentDetails, callbacks));
      } else {
        const { selectedAlerts } = this.getProperties('selectedAlerts');
        dispatch(createIncidentFromAlerts(incidentDetails, selectedAlerts, callbacks));
      }
    }
  };
};
/**
 * @class CreateIncident
 * The form (with validation) required to create an incident from one or more alerts
 *
 * @public
 */
const createIncidentButton = Component.extend(Notifications, {
  layout,
  i18n: service(),

  classNames: ['rsa-create-incident'],
  /**
   * Represents the (required) name that will be used to create the incident
   * @property name
   * @type {string}
   * @public
   */
  name: null,

  /**
   * Represents the (required) priority that will be set on the newly created incident
   * @property priority
   * @type {string}
   * @public
   */
  priority: 'LOW',

  /**
   * Represents the (optional) assignee who will be set on the newly created incident
   * @property assignee
   * @type {object}
   * @public
   */
  assignee: null,

  /**
   * Represents the (optional) category that will be set on the newly created incident
   * @property category
   * @type {object}
   * @public
   */
  categories: null,

  /**
   * Represents the alert name/summary that will be set on all the alerts created from the selected events (internally)
   * @property alertSummary
   * @type {string}
   * @public
   */
  alertSummary: '',

  /**
   * Represents the alert Severity that will be set on all the alerts created from the selected events (internally)
   * @property alertSeverity
   * @type {number}
   * @public
   */
  alertSeverity: 50,

  /**
   * Represents the selected events that has to go into an incident, passed from investigation-events
   * @property selectedEvents
   * @type {[]}
   * @public
   */
  selectedEventIds: null,

  /**
   * Represents the selected serviceId (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property selectedEndpointId
   * @type {string}
   * @public
   */
  endpointId: null,

  /**
   * Represents the query start Time (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property startTime
   * @type {long}
   * @public
   */
  startTime: null,

  /**
   * Represents the query end Time (passed from) on the investigation-events Page, required to be sent to
   * Investigation Server for incident creation
   * @property endTime
   * @type {long}
   * @public
   */
  endTime: null,

  /**
   * Represents the selected alerts that has to go into an incident, passed from respond
   * @property selectedAlerts
   * @type {[]}
   * @public
   */
  selectedAlerts: null,

  /**
   * Represents whether alert severity entered by user is valid or not
   * @property isAlertSeverityInvalid
   * @type {boolean}
   * @public
   */
  isAlertSeverityInvalid: false,

  /**
   * Represents whether incident create action is in progress
   * @property isCreateInProgress
   * @type {boolean}
   * @public
   */
  isCreateInProgress: false,

  /**
   * Flag to control enable/disable behaviour of create incident button.
   * True if incident name is empty or alert severity which user entered is incorrect or create incident action started
   * @property isDisabled
   * @type {boolean}
   * @public
   */
  isDisabled: computed('name', 'isAlertSeverityInvalid', 'isCreateInProgress', function() {
    return isEmpty(this.name) || typeOf(this.name) === 'string' && isEmpty(this.name?.trim()) || this.isAlertSeverityInvalid || this.isCreateInProgress;
  }),

  didInsertElement() {
    this._super(...arguments);
    this.set('alertSummary', this.get('i18n').t('respond.alerts.defaultAlertSummaryText'));
  },

  actions: {
    alertSeverityChanged(alertSeverity) {
      // Regex to match number between 1 and 100
      const validSeverityRegex = /^[1-9][0-9]?$|^100$/;

      const isAlertSeverityInvalid = !validSeverityRegex.test(alertSeverity);

      this.set('isAlertSeverityInvalid', isAlertSeverityInvalid);
    },

    handleCancel() {
      this.close();
    },
    handleCreate() {
      this.set('isCreateInProgress', true);
      const { name, priority, assignee, categories } = this.getProperties('name', 'priority', 'assignee', 'categories');
      let incidentDetails = {
        name,
        priority,
        assignee,
        categories
      };
      if (this.get('selectedEventIds')) {
        const { alertSummary, alertSeverity, selectedEventIds, endpointId, startTime, endTime } = this.getProperties(
          'alertSummary', 'alertSeverity', 'selectedEventIds', 'endpointId', 'startTime', 'endTime');
        if (assignee) {
          incidentDetails.assignee = assignee.id;
        }
        incidentDetails = {
          ...incidentDetails,
          eventIds: selectedEventIds,
          endpointId,
          range: {
            from: startTime,
            to: endTime
          },
          alertSummary,
          alertSeverity: Number.parseInt(alertSeverity, 10)
        };
      }
      this.send('createIncident', incidentDetails);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(createIncidentButton);
