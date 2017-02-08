/**
 * @file Default (index) route
 * Defines the default route when no top-level route is given. Eventually, the logic for choosing the default
 * could be sophisticated (e.g., it could depend on the user's roles/permissions). For now, we hard-code.
 * @public
 */
import Ember from 'ember';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend({

  landingPage: service(),

  beforeModel(transition) {
    const redirect = localStorage.getItem('rsa-post-auth-redirect');
    const key = this.get('landingPage.selected.key');

    if (redirect) {
      localStorage.removeItem('rsa-post-auth-redirect');
    }

    if (redirect && redirect != transition.targetName) {
      this.transitionTo(redirect);
    } else {
      switch (key) {
        case '/investigate':
          this.transitionTo(key);
          break;
        case '/respond':
          this.transitionTo(key);
          break;
        default:
          window.location.href = key;
      }
    }
  }
});
