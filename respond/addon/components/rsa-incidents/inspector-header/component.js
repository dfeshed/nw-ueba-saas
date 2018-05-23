import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import * as incidentCreators from 'respond/actions/creators/incidents-creators';
import { isIncidentClosed } from 'respond/helpers/is-incident-closed';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';

const dispatchToActions = (dispatch) => {
  return {
    escalate(incidentId) {
      dispatch(incidentCreators.escalate(incidentId, {
        onSuccess: () => (this.send('success', 'respond.incidents.actions.actionMessages.escalationSuccess', { incidentId })),
        onFailure: (response = {}) => {
          const { code } = response;
          const errorCodesToI18n = {
            31: 'sendToArcherConnectionFailed',
            32: 'sendToArcherMetadataLoadFailed',
            33: 'sendToArcherValidationFailed'
          };
          const i18nFailureKey = `respond.incidents.actions.actionMessages.${(errorCodesToI18n[code] || 'sendToArcherFailed')}`;
          this.send('failure', i18nFailureKey, { incidentId }, { sticky: true });
        }
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
    return isIncidentClosed(status) || canManageIncidents === false;
  },
  actions: {
    updateName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});

export default connect(undefined, dispatchToActions)(IncidentInspectorHeader);