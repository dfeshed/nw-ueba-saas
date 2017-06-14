import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import computed from 'ember-computed-decorators';
import moment from 'moment';
import { updateItem, deleteItem } from 'respond/actions/creators/remediation-task-creators';
import Confirmable from 'respond/mixins/confirmable';
import Notifications from 'respond/mixins/notifications';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';

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

  @computed('info.status')
  isOpen(status) {
    return !closedStatuses.includes(status);
  },

  @computed('info.created')
  timeSinceCreation(created) {
    return moment(created).toNow(true);
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