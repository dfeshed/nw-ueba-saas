import Ember from 'ember';
import computed from 'ember-computed-decorators';

const {
  Component,
  inject: {
    service
    },
  Logger
  } = Ember;

export default Component.extend({
  classNames: ['rsa-journal-wrapper', 'spacer'],

  session: service(),

  viewNotes: 'all',
  model: null,

  @computed('session')
  currentUser(session) {
    let username;

    if (session) {
      username = session.session.content.authenticated.username;
    } else {
      username = '-';
      Logger.error('unable to read current username');
    }
    return username;
  },

  /**
   * @description it determinate if a journal entry must be visible based on the selected filter (all | my)
   * @returns {boolean}
   * @public
   */
  displayJournal(journal, viewNotes) {
    return (viewNotes === 'all' || journal.author === this.get('currentUser'));
  }
});
