import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { updateItem, deleteItem } from 'respond/actions/creators/remediation-task-creators';
import Confirmable from 'respond/mixins/confirmable';
import Notifications from 'respond/mixins/notifications';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import { inject as service } from '@ember/service';

const closedStatuses = ['REMEDIATED', 'RISK_ACCEPTED', 'NOT_APPLICABLE'];

const stateToComputed = (state) => {
  const {
    respond: {
      dictionaries
    }
  } = state;

  return {
    priorityTypes: dictionaries.priorityTypes,
    remediationStatusTypes: dictionaries.remediationStatusTypes
  };
};

const dispatchToActions = (dispatch) => {
  return {
    update(entityId, field, updatedValue, revertCallback = () => {}) {
      dispatch(updateItem(entityId, field, updatedValue, {
        onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
        onFailure: () => {
          revertCallback();
          this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure');
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
          onSuccess: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess')),
          onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.updateFailure'))
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
  @computed('info.status')
  isOpen(status) {
    return !closedStatuses.includes(status);
  },

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
