import Ember from 'ember';
import IncidentConstants from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
    },
  Logger
  } = Ember;

export default Component.extend({
  tagName: 'div',
  classNames: 'rsa-journal-entry',
  classNameBindings: ['editModeActive', 'addMode'],

  eventBus: service(),
  session: service(),

  // Indicate if the component is used to create new Journal Entries or to view/edit existing ones
  addMode: false,
  // When true the JournalEntry is editable
  editModeActive: false,

  // Used to store new journal values
  newNote: null,
  newMilestone: null,

  // Initial model values used to rollback if user press Cancel
  initMilestone: null,
  initNotes: null,

  didReceiveAttrs() {
    this._super(...arguments);

    if (this.get('addMode') === false) {
      this.setProperties({
        'initMilestone': this.get('journal.milestone'),
        'initNotes': this.get('journal.notes')
      });
    }
  },

  /**
   * @description Defines when a User can edit or only-view a journal entry
   * return {boolean}
   * @public
   */
  @computed('addMode', 'editModeActive')
  isReadOnly(addMode, editModeActive) {
    let isEditable = (addMode === true || editModeActive === true);
    return isEditable;
  },

  @computed('session')
  currentUser(session) {
    let username;

    if (session) {
      username = session.get('session.content.authenticated.user.id');
    } else {
      username = '-';
      Logger.error('unable to read current username');
    }
    return username;
  },

  /**
   * @description List of milestone options
   * @public
   */
  milestoneOptions: Object.keys(IncidentConstants.journalMilestones),

  /**
   * @description selected milestone option
   * @public
   */
  @computed('journal.milestone')
  selectedMilestone: {
    get: (milestone) => (milestone ? [ milestone ] : []),
    set(value) {
      this.set('journal.milestone', value.get('firstObject'));
      return value;
    }
  },

  /**
   * @description selected milestone option when adding a new Journal
   * @public
   */
  @computed('newMilestone')
  newMilestoneSelect: {
    get: () => [],
    set(value) {
      this.set('newMilestone', value.get('firstObject'));
      return value;
    }
  },

  /**
   * @description Set to edit-mode when user clicks on the component for 'as-new' entries
   * @public
   */
  click() {
    if (this.get('addMode') === true && this.get('editModeActive') === false) {
      this.set('editModeActive', true);
    }
  },

  actions: {
    /**
     * @description Enables to edit the current journal entry
     * @private
     */
    editJournal(event) {
      this.set('editModeActive', true);
      event.stopPropagation();
    },

    /**
     * @description Reverts all temp modifications and returns to view-only mode
     * @private
     */
    cancelEdit(event) {
      if (this.get('addMode') === false) {
        // rollback journal values
        this.setProperties({
          'journal.milestone': this.get('initMilestone'),
          'journal.notes': this.get('initNotes')
        });
      } else {
        // reset new-journey values
        this.setProperties({
          newNote: null,
          newMilestone: null,
          newMilestoneSelect: []
        });
      }

      this.set('editModeActive', false);
      event.stopPropagation();
    },

    /**
     * @description Saves the Journal Entry. If the component is in `add-mode` it invokes `addJournalAction`,
     * otherwise `editJournalAction`
     * @public
     */
    saveJournal(event) {

      if (this.get('addMode') === true) {
        // Creates a new JournalEntry
        let newMilestone = this.get('newMilestone');
        let newAuthor = this.get('currentUser');
        let newNote = this.get('newNote');

        let newJournal = {
          notes: newNote,
          milestone: newMilestone,
          author: newAuthor
        };

        this.sendAction('saveJournalAction', newJournal);

        // clear editable fields
        this.setProperties({
          newNote: null,
          newMilestone: null,
          newMilestoneSelect: []
        });
      } else {
        // Edit existing JournalEntry
        this.sendAction('saveJournalAction', this.get('journal'));
      }

      this.set('editModeActive', false);
      event.stopPropagation();
    },

    /**
     * @description Cancel the delete operation by dismising the delete-modal-dialog
     * @public
     */
    cancelDelete() {
      this.get('eventBus').trigger('rsa-application-modal-close-deleteJournalEntry');
    },

    /**
     * @description Invokes delete action and dismiss delete-modal-dialog
     * @public
     */
    confirmDelete() {
      this.sendAction('deleteJournalAction', this.get('journal.id'));

      this.get('eventBus').trigger('rsa-application-modal-close-deleteJournalEntry');
      this.set('editModeActive', false);
    }
  }
});
