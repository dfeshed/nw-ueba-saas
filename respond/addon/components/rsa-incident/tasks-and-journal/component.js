import { connect } from 'ember-redux';
import {
  toggleTasksAndJournalPanel,
  setTasksJournalMode
} from 'respond/actions/creators/incidents-creators';
import Component from 'ember-component';
import computed, { alias } from 'ember-computed-decorators';
import service from 'ember-service/inject';

const closedStatuses = ['CLOSED', 'CLOSED_FALSE_POSITIVE'];

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
  accessControl: service(),

  isAddingNewTask: false,

  @computed('info.status')
  isIncidentClosed(status) {
    return closedStatuses.includes(status);
  },

  // The incident's notes can be undefined/null, in which case default to zero
  @computed('info.notes.length')
  journalEntryCount(notesCount) {
    return notesCount || 0;
  },

  @alias('tasks.length') taskCount: 0,

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