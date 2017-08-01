import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { createIncidentFromAlerts } from 'respond/actions/creators/incidents-creators';
import Notifications from 'respond/mixins/notifications';
import { isEmpty, typeOf } from 'ember-utils';

const stateToComputed = (state) => {
  const { respond: { alerts: { itemsSelected } } } = state;

  return {
    alertIds: itemsSelected
  };
};

const dispatchToActions = (dispatch) => {
  return {
    create(incidentName, alertIds, callbacks) {
      dispatch(createIncidentFromAlerts(incidentName, alertIds, callbacks));
    }
  };
};

/**
 * @class CreateIncident
 * The form (with validation) required to create an incident from one or more alerts
 *
 * @public
 */
const CreateIncident = Component.extend(Notifications, {
  classNames: ['rsa-create-incident'],
  /**
   * Represents the (required) name that will be used to create the incident
   * @property incidentName
   * @type {string}
   * @public
   */
  incidentName: null,

  /**
   * Tracks whether or not the creation operation is currently in progress. This would be set to true just before
   * the call is made to the server, and then reset to false as soon as the call to the server has completed.
   * @property isCreationInProgress
   * @type {boolean}
   * @public
   */
  isCreationInProgress: false,

  /**
   * Indicates whether the form is invalid. Since the form only has one field (for incident name) and that field is
   * required for incident creation, the form is only invalid if the field is empty
   * @property isInvalid
   * @type {boolean}
   * @public
   */
  @computed('incidentName')
  isInvalid(name) {
    return isEmpty(name) || typeOf(name) === 'string' && isEmpty(name.trim());
  },

  actions: {
    handleCancel() {
      this.sendAction('close');
    },
    handleApply() {
      const { incidentName, alertIds } = this.getProperties('alertIds', 'incidentName');
      this.set('isCreationInProgress', true);
      this.send('create', incidentName, alertIds, {
        // Close the modal and show success notification to the user, if the create call has succeeded
        onSuccess: (response) => {
          this.sendAction('close');
          this.send('success', 'respond.incidents.actions.actionMessages.incidentCreated', { incidentId: response.data.id });
          this.set('isCreationInProgress', false);
        },
        // Show a failure notification if the create call has failed
        onFailure: () => {
          this.send('failure', 'respond.incidents.actions.actionMessages.incidentCreationFailed');
          this.set('isCreationInProgress', false);
        }
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CreateIncident);