import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import * as incidentCreators from 'respond/actions/creators/incidents-creators';
import Notifications from 'respond/mixins/notifications';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import computed from 'ember-computed-decorators';

const dispatchToActions = (dispatch) => {
  return {
    escalate(incidentId) {
      dispatch(incidentCreators.escalate(incidentId, {
        onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.incidents.actions.actionMessages.escalationSuccess', { incidentId })),
        onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.incidents.actions.actionMessages.escalationFailure', { incidentId }))
      }));
    }
  };
};

const IncidentInspectorHeader = Component.extend(Notifications, {
  classNames: ['incident-inspector-header'],
  accessControl: service(),
  @computed('info.status', 'info.escalationStatus', 'accessControl.respondCanManageIncidents')
  isEscalationDisabled(status, escalationStatus, canManageIncidents) {
    const closedStatuses = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];
    return closedStatuses.includes(status) || canManageIncidents === false;
  },
  actions: {
    updateName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});

export default connect(undefined, dispatchToActions)(IncidentInspectorHeader);