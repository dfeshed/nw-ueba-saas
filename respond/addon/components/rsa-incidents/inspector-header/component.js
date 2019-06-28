import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import * as incidentCreators from 'respond/actions/creators/incidents-creators';
import { isIncidentClosed } from 'respond/helpers/is-incident-closed';
import Notifications from 'component-lib/mixins/notifications';
import computed from 'ember-computed-decorators';

const dispatchToActions = (dispatch) => {
  return {
    sendToArcher(incidentId) {
      dispatch(incidentCreators.sendToArcher(incidentId, {
        onSuccess: (response) => (this.send('success', 'respond.incidents.actions.actionMessages.sendToArcherSuccess', {
          incidentId,
          archerIncidentId: response.meta.archerIncidentId
        })),
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
   * Indicates whether or not the send incident to archer feature is available. If so, the Send to Archer button will
   * be shown in the inspector header. Currently, send-to-archer will only be available if an Archer data source
   * is configured in Context Hub. Default: false
   * @property isSendToArcherAvailable
   * @public
   */
  isSendToArcherAvailable: false,

  @computed('info.status', 'accessControl.respondCanManageIncidents')
  isSendToArcherDisabled(status, canManageIncidents) {
    return isIncidentClosed(status) || canManageIncidents === false;
  },
  actions: {
    updateName(entityId, fieldName, updatedValue, originalValue, revertCallback) {
      this.get('updateItem')(entityId, fieldName, updatedValue, revertCallback);
    }
  }
});

export default connect(undefined, dispatchToActions)(IncidentInspectorHeader);