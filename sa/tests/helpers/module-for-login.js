import { module } from 'qunit';
import Ember from 'ember';
import startApp from '../helpers/start-app';
import destroyApp from '../helpers/destroy-app';
import teardownSockets from 'sa/tests/helpers/teardown-sockets';
import { invalidateSession } from 'sa/tests/helpers/ember-simple-auth';
import Authenticator from 'component-lib/authenticators/oauth-authenticator';

const { RSVP: { resolve } } = Ember;

const patchTokenRefresh = () => {
  Authenticator.reopen({
    _scheduleAccessTokenRefresh() {
    }
  });
};

export default function(name, options = {}) {
  module(name, {
    beforeEach() {
      patchTokenRefresh();

      this.application = startApp();

      invalidateSession(this.application);
      localStorage.removeItem('rsa-oauth2-jwt-access-token');
      localStorage.removeItem('rsa-post-auth-redirect');
      localStorage.setItem('rsa::netWitness::eulaAccepted', true);

      if (options.beforeEach) {
        return options.beforeEach.apply(this, arguments);
      }
    },

    afterEach() {
      localStorage.removeItem('rsa::netWitness::eulaAccepted');
      teardownSockets.apply(this);
      const afterEach = options.afterEach && options.afterEach.apply(this, arguments);
      return resolve(afterEach).then(() => destroyApp(this.application));
    }
  });
}
