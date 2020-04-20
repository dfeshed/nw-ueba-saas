import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updateItem, deleteItem } from 'respond/actions/creators/remediation-task-creators';
import Confirmable from 'component-lib/mixins/confirmable';
import Notifications from 'component-lib/mixins/notifications';
import { inject as service } from '@ember/service';

const closedStatuses = ['REMEDIATED', 'RISK_ACCEPTED', 'NOT_APPLICABLE'];

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries
    },
    respondShared: {
      createIncident
    }
  } = state;

  return {
    priorityTypes: createIncident.priorityTypes,
    remediationStatusTypes: dictionaries.remediationStatusTypes
  };
};

const dispatchToActions = (dispatch) => {
  return {
    update(entityId, field, updatedValue, revertCallback = () => {}) {
      dispatch(updateItem(entityId, field, updatedValue, {
        onSuccess: () => (this.send('success', 'respond.entities.actionMessages.updateSuccess')),
        onFailure: () => {
          revertCallback();
          this.send('failure', 'respond.entities.actionMessages.updateFailure');
        }
      }));
    },

    delete(taskId) {
      const { i18n } = this.getProperties('i18n');
      this.send('showConfirmationDialog', 'delete-task', {
        count: 1,
        warning: i18n.t('respond.remediationTasks.actions.actionMessages.deleteWarning')
      }, () => {
        dispatch(deleteItem(taskId, {
          onSuccess: () => (this.send('success', 'respond.entities.actionMessages.updateSuccess')),
          onFailure: () => (this.send('failure', 'respond.entities.actionMessages.updateFailure'))
        }));
      });
    }
  };
};

const RemediationTask = Component.extend(Notifications, Confirmable, {
  classNames: ['remediation-task'],
  accessControl: service(),
  /**
   * Returns true if the status is one of the open types, or false if one of the closed types (Remediated, Risk
   * Accepted, or Not Applicable)
   * @param status
   * @returns {boolean}
   * @public
   */
  isOpen: computed('info.status', function() {
    return !closedStatuses.includes(this.info?.status);
  }),

  actions: {
    selectionChange(entityId, field, updatedValue) {
      this.send('update', entityId, field, updatedValue);
    },

    editableFieldChange(entityId, field, updatedValue, originalValue, revertCallback) {
      this.send('update', entityId, field, updatedValue, revertCallback);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(RemediationTask);
