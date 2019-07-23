import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import getOwner from 'ember-owner/get';

/**
 * This route is responsible for initiating OAuth token authentication. The user will be redirected to this route after
 * SSO authentication is successful in admin-server
 * Redirection URL looks like - /sso-oauth?authkey={}&relay=/respond
 * authkey - unique randomly generated authentication key used to map this SSO authenticated user in the server
 * relay - URL to redirect after successful auth
 */
export default Route.extend({
  session: service(),

  queryParams: {
    authkey: { refreshModel: false },
    relay: { refreshModel: false },
    user: { refreshModel: false }
  },

  model({ user, authkey, relay = '' }) {
    localStorage.setItem('rsa-post-auth-redirect', relay);
    this.authenticate(user, authkey);
  },

  authenticate(user, authkey) {
    const session = this.get('session');

    // Calls the custom sa-authenticator app/authenticators/sa-authenticator
    const config = getOwner(this).resolveRegistration('config:environment');
    const auth = config['ember-simple-auth'].authenticate;

    // Authenticate SAML user
    session.authenticate(auth, user, authkey, 'saml').then(
      // Auth success
      () => {
        // nothing
      },

      // Auth failed
      () => {
        this.transitionTo('sso-error');
      }
    ).catch(() => {
      this.transitionTo('sso-error');
    });
  }
});
