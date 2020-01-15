import { computed } from '@ember/object';
import { connect } from 'ember-redux';
import {
  toggleTasksAndJournalPanel,
  setTasksJournalMode
} from 'respond/actions/creators/incidents-creators';
import Component from '@ember/component';
import { alias } from '@ember/object/computed';
import { inject as service } from '@ember/service';
import { isIncidentClosed } from 'respond/helpers/is-incident-closed';

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

  isIncidentClosed: computed('info.status', function() {
    return isIncidentClosed(this.info?.status);
  }),

  // The incident's notes can be undefined/null, in which case default to zero
  journalEntryCount: computed('info.notes.length', function() {
    return this.info?.notes?.length || 0;
  }),

  taskCount: alias('tasks.length'),

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
