import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import * as incidentCreators from 'respond/actions/creators/incidents-creators';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';

const dispatchToActions = (dispatch) => {
  return {
    escalate(incidentId) {
      dispatch(incidentCreators.escalate(incidentId, {
        onSuccess: () => (this.send('success', 'respond.incidents.actions.actionMessages.escalationSuccess', { incidentId })),
        onFailure: () => (this.send('failure', 'respond.incidents.actions.actionMessages.escalationFailure', { incidentId }))
      }));
    }
  };
};

const IncidentInspectorHeader = Component.extend(Notifications, {
  classNames: ['incident-inspector-header'],
  accessControl: service(),

  /**
   * Indicates whether or not the escalate incident feature is available. If so, the escalate button will
   * be shown in the inspector header. Currently, escalate will only be available if an Archer data source
   * is configured in Context Hub. Default: false
   * @property isEscalateAvailable
   * @public
   */
  isEscalateAvailable: false,

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