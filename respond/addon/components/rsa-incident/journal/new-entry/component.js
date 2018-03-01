import Component from '@ember/component';
import { connect } from 'ember-redux';
import { createJournalEntry } from 'respond/actions/creators/journal-creators';
import { empty } from 'ember-computed-decorators';
import Notifications from 'respond/mixins/notifications';
import FLASH_MESSAGE_TYPES from 'respond/utils/flash-message-types';

const stateToComputed = ({ respond: { dictionaries: { milestoneTypes } } }) => ({
  milestoneTypes
});

const dispatchToActions = (dispatch) => {
  return {
    createEntry(entry) {
      dispatch(createJournalEntry(entry, {
        onSuccess: () => {
          this.send('showFlashMessage', FLASH_MESSAGE_TYPES.SUCCESS, 'respond.entities.actionMessages.updateSuccess');
          this.setProperties({ notes: null, milestone: null });
        },
        onFailure: () => (this.send('showFlashMessage', FLASH_MESSAGE_TYPES.ERROR, 'respond.entities.actionMessages.createFailure'))
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
