import Component from 'ember-component';
import { connect } from 'ember-redux';
import Notifications from 'respond/mixins/notifications';
import Confirmable from 'respond/mixins/confirmable';
import { deleteJournalEntry, updateJournalEntry } from 'respond/actions/creators/journal-creators';
import layout from './template';
import service from 'ember-service/inject';

const stateToComputed = ({ respond: { dictionaries: { milestoneTypes } } }) => ({
  milestoneTypes
});

const dispatchToActions = (dispatch) => {
  return {
    deleteEntry(incidentId, entryId) {
      this.send('showConfirmationDialog', 'delete-journal-entry', {}, () => {
        dispatch(deleteJournalEntry(incidentId, entryId, {
          onSuccess: () => (this.send('success', 'respond.entities.actionMessages.updateSuccess')),
          onFailure: () => (this.send('failure', 'respond.entities.actionMessages.deleteFailure'))
        }));
      });
    },
    updateEntry(incidentId, entryId, journalMap) {
      dispatch(updateJournalEntry(incidentId, entryId, journalMap, {
        onSuccess: () => (this.send('success', 'respond.entities.actionMessages.updateSuccess')),
        onFailure: () => (this.send('failure', 'respond.entities.actionMessages.updateFailure'))
      }));
    }
  };
};

const JournalEntry = Component.extend(Notifications, Confirmable, {
  layout,
  classNames: ['rsa-incident-journal-entry'],
  accessControl: service(),
  /**
   * The journal entry data.
   * @type { author: String, created: String, notes: String, milestone: String }
   * @public
   */
  entry: null,

  actions: {
    handleDelete() {
      const { incidentId, entry } = this.getProperties('incidentId', 'entry');
      this.send('deleteEntry', incidentId, entry.id);
    },
    handleNoteChange(updatedNote) {
      const { incidentId, entry } = this.getProperties('incidentId', 'entry');
      this.send('updateEntry', incidentId, entry.id, {
        notes: updatedNote
      });
    },
    handleMilestoneChange(milestone) {
      const { incidentId, entry } = this.getProperties('incidentId', 'entry');
      this.send('updateEntry', incidentId, entry.id, {
        milestone
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(JournalEntry);