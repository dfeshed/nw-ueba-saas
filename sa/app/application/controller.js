import computed from 'ember-computed';
import Controller from 'ember-controller';
import service from 'ember-service/inject';

export default Controller.extend({
  fatalErrors: service(),

  session: service(),

  accessControl: service(),

  authenticatedAndPageFound: computed('session.isAuthenticated', 'currentPath', function() {
    const path = this.get('currentPath');

    if (!this.get('session.isAuthenticated') || path === 'not-found' || path === 'internal-error') {
      return false;
    } else {
      return true;
    }
  })
});
