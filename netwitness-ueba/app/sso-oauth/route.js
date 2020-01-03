import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import getOwner from 'ember-owner/get';

/**
 * This route is responsible for initiating OAuth token authentication. The user will be redirected to this route after
 * SSO authentication is successful in admin-server
 * Redirection URL looks like - /sso-oauth?user={}&authkey={}
 * user - user to be authenticated
 * authkey - unique randomly generated authentication key used to map this SSO authenticated user in the server
 */
export default Route.extend({
  session: service(),

  queryParams: {
    authkey: { refreshModel: false },
    user: { refreshModel: false }
  },

  model({ user, authkey }) {
    return this.authenticate(user, authkey);
  },

  authenticate(user, authkey) {
    const session = this.get('session');

    // Calls the custom sa-authenticator app/authenticators/sa-authenticator
    const config = getOwner(this).resolveRegistration('config:environment');
    const auth = config['ember-simple-auth'].authenticate;

    // Authenticate SAML user
    return session.authenticate(auth, user, authkey, 'saml');
  },

  afterModel(model, transition) {
    transition.abort();
    this.transitionTo('protected');
  },

  actions: {
    error() {
      this.transitionTo('sso-error');
    }
  }
});
