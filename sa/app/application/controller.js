import computed from 'ember-computed';
import config from 'ember-get-config';
import Controller from 'ember-controller';
import service from 'ember-service/inject';

export default Controller.extend({
  fatalErrors: service(),

  session: service(),

  accessControl: service(),

  linkTo11: config.featureFlags['11.1-enabled'],

  authenticatedAndPageFound: computed('session.isAuthenticated', 'currentPath', function() {
    const path = this.get('currentPath');

    if (!this.get('session.isAuthenticated') || path === 'not-found') {
      return false;
    } else {
      return true;
    }
  })
});
