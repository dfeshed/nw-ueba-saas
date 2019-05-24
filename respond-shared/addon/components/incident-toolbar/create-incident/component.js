import layout from './template';
import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';
import { isEmpty, typeOf } from '@ember/utils';
import {
  getEnabledUsers,
  getGroupedCategories,
  getPriorityTypes
} from '../../../selectors/create-incident/selectors';
import { createIncidentFromEvents, createIncidentFromAlerts } from 'respond-shared/actions/creators/create-incident-creators';
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
   * @property alertName
   * @type {string}
   * @public
   */
  alertName: '',

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
   * Indicates whether the form is invalid. Since the form only has one field (for incident name) and that field is
   * required for incident creation, the form is only invalid if the field is empty
   * @property isInvalid
   * @type {boolean}
   * @public
   */
  @computed('name')
  isInvalid(name) {
    return isEmpty(name) || typeOf(name) === 'string' && isEmpty(name.trim());
  },

  didInsertElement() {
    this._super(...arguments);
    this.set('alertName', this.get('i18n').t('respond.alerts.defaultAlertSummaryText').string);
  },

  actions: {
    handleCancel() {
      this.close();
    },
    handleCreate() {
      const { name, priority, assignee, categories } = this.getProperties('name', 'priority', 'assignee', 'categories');
      let incidentDetails = {
        name,
        priority,
        assignee,
        categories
      };
      if (this.get('selectedEventIds')) {
        const { alertName, alertSeverity, selectedEventIds, endpointId, startTime, endTime } = this.getProperties(
          'alertName', 'alertSeverity', 'selectedEventIds', 'endpointId', 'startTime', 'endTime');
        if (assignee) {
          incidentDetails.assignee = assignee.id;
        }
        incidentDetails = { ...incidentDetails,
          eventIds: selectedEventIds,
          endpointId,
          range: {
            from: startTime,
            to: endTime
          },
          alertSummary: alertName,
          alertSeverity: Number.parseInt(alertSeverity, 10)
        };
      }
      this.send('createIncident', incidentDetails);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(createIncidentButton);
