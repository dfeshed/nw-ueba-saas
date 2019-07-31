import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';
import { windowProxy } from 'component-lib/utils/window-proxy';
import fetch from 'component-lib/utils/fetch';

/**
  Responsible for making the login route available to parent application.
  @public
*/
export default Route.extend({
  session: service(),

  checkingSso: true,

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
      if (response) {
        // redirect to ADFS login page
        windowProxy.openInCurrentTab('/saml/login');
      } else {
        this.set('checkingSso', false);
      }
      return { checkingSso: this.get('checkingSso') };
    }).catch(() => {
      // eslint-disable-next-line no-console
      console.log('fetching sso status failed');
      this.set('checkingSso', false);
    });
  }
});
