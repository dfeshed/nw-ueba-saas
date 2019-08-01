import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import fetch from 'component-lib/utils/fetch';

/**
 Responsible for making the login route available to parent application.
 @public
 */
export default Route.extend({
  session: service(),

  beforeModel() {
    if (this.get('session.isAuthenticated')) {
      this.transitionTo('protected');
    }
  },

  model() {
    return this.checkSsoStatus();
  },

  checkSsoStatus() {
    return fetch('/saml/sso/is-enabled').then((fetched) => fetched.json()).then((response) => {
      return { isSsoEnabled: response };
    }).catch(() => {
      return { isSsoEnabled: false };
    });
  }
});
