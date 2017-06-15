import connect from 'ember-redux/components/connect';
import { toggleTasksAndJournalPanel, setTasksJournalMode } from 'respond/actions/creators/incidents-creators';
import Component from 'ember-component';

const stateToComputed = (state) => {
  const { respond: { incident: { id, info, isShowingTasksAndJournal, tasks, tasksStatus, tasksJournalMode } } } = state;
  return {
    incidentId: id,
    info,
    tasks,
    tasksStatus,
    isShowingTasksAndJournal,
    tasksJournalMode
  };
};

const dispatchToActions = (dispatch) => ({
  toggleTasksAndJournalPanel() {
    dispatch(toggleTasksAndJournalPanel());
  },
  setTasksJournalMode(viewMode) {
    dispatch(setTasksJournalMode(viewMode));
  }
});

const IncidentTasksJournalPanel = Component.extend({
  attributeBindings: ['style'],
  tagName: 'article',
  classNames: ['rsa-journal-and-tasks'],
  isAddingNewTask: false,
  actions: {
    addNewTask() {
      this.set('isAddingNewTask', true);
    },
    cancelNewTask() {
      this.set('isAddingNewTask', false);
    },
    onTaskCreated() {
      this.set('isAddingNewTask', false);
    },
    showTab(tabName) {
      this.send('setTasksJournalMode', tabName);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentTasksJournalPanel);