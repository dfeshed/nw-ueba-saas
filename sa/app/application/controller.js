import Ember from 'ember';

const {
  Controller,
  inject: {
    service
  },
  computed
} = Ember;

export default Controller.extend({
  fatalErrors: service(),

  session: service(),

  accessControl: service(),

  authenticatedAndPageFound: computed('session.isAuthenticated', 'currentPath', function() {
    const path = this.get('currentPath');

    if (!this.get('session.isAuthenticated') || path === 'not-found') {
      return false;
    } else {
      return true;
    }
  })
});
