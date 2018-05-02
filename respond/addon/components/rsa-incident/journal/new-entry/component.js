import Component from '@ember/component';
import { connect } from 'ember-redux';
import { createJournalEntry } from 'respond/actions/creators/journal-creators';
import { empty } from 'ember-computed-decorators';
import Notifications from 'component-lib/mixins/notifications';

const stateToComputed = ({ respond: { dictionaries: { milestoneTypes } } }) => ({
  milestoneTypes
});

const dispatchToActions = (dispatch) => {
  return {
    createEntry(entry) {
      dispatch(createJournalEntry(entry, {
        onSuccess: () => {
          this.send('success', 'respond.entities.actionMessages.updateSuccess');
          this.setProperties({ notes: null, milestone: null });
        },
        onFailure: () => (this.send('failure', 'respond.entities.actionMessages.createFailure'))
      }));
    }
  };
};

const Journal = Component.extend(Notifications, {
  classNames: ['rsa-incident-journal__new-entry'],

  incidentId: null,
  notes: null,
  milestone: null,

  @empty('notes') isInvalid: true,

  actions: {
    handleSubmit() {
      const { incidentId, notes, milestone } = this.getProperties('incidentId', 'notes', 'milestone');
      this.send('createEntry', {
        incidentId,
        notes,
        milestone
      });
    },
    handleMilestoneChange(milestone) {
      this.set('milestone', milestone);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Journal);
