import Component from 'ember-component';
import connect from 'ember-redux/components/connect';
import { createItem } from 'respond/actions/creators/remediation-task-creators';
import computed from 'ember-computed-decorators';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import Notifications from 'respond/mixins/notifications';

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
    createTask(task) {
      dispatch(createItem(task, {
        onSuccess: () => {
          this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess');
          this.get('onCreated')();
        },
        onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.createFailure'))
      }));
    }
  };
};

const NewRemediationTask = Component.extend(Notifications, {
  classNames: ['new-remediation-task'],

  incidentId: null,
  name: null,
  description: null,
  assignee: null,
  priority: null,
  status: 'NEW',
  cancelNewTask() {},
  submitNewTask() {},

  @computed('name', 'priority')
  isValid(name, priority) {
    return name && priority;
  },
  actions: {
    handlePriorityChange(priority) {
      this.set('priority', priority);
    },
    handleStatusChange(status) {
      this.set('status', status);
    },
    handleCancel() {
      this.get('onCancel')();
    },
    handleSubmit() {
      this.send('createTask', this.getProperties('incidentId', 'name', 'description', 'assignee', 'priority', 'status'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(NewRemediationTask);