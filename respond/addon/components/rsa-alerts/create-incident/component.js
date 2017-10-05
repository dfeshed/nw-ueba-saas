import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import Notifications from 'respond/mixins/notifications';
import { isEmpty, typeOf } from 'ember-utils';
import * as ACTION_TYPES from '../../../actions/types';

const stateToComputed = (state) => {
  const { respond: { alerts: { itemsSelected } } } = state;

  return {
    alertIds: itemsSelected
  };
};

const dispatchToActions = function(dispatch) {
  return {
    create: () => {
      const { incidentName, alertIds } = this.getProperties('alertIds', 'incidentName');
      dispatch({ type: ACTION_TYPES.CREATE_INCIDENT_SAGA, incidentName, alertIds });
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CreateIncident);
