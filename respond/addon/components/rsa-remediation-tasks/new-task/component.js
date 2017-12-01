import Component from 'ember-component';
import { connect } from 'ember-redux';
import { createItem } from 'respond/actions/creators/remediation-task-creators';
import computed from 'ember-computed-decorators';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';
import Notifications from 'respond/mixins/notifications';
import { getPriorityTypes } from 'respond/selectors/dictionaries';
import { getTasksStatus } from 'respond/selectors/incidents';

const stateToComputed = (state) => {
  return {
    priorityTypes: getPriorityTypes(state),
    tasksStatus: getTasksStatus(state)
  };
};

const dispatchToActions = (dispatch) => {
  return {
    createTask(task) {
      dispatch(createItem(task, {
        onSuccess: () => {
          this.set('isTaskCreated', true);
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
  cancelNewTask() {},
  submitNewTask() {},

  @computed('name', 'priority', 'tasksStatus')
  isValid(name, priority, tasksStatus) {
    return name && priority && tasksStatus !== 'creating';
  },
  actions: {
    handlePriorityChange(priority) {
      this.set('priority', priority);
    },
    handleCancel() {
      this.get('onCancel')();
    },
    handleSubmit() {
      this.send('createTask', this.getProperties('incidentId', 'name', 'description', 'assignee', 'priority'));
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(NewRemediationTask);