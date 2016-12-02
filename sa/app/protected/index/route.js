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

  beforeModel() {
    const key = this.get('landingPage.selected.key');

    if (key) {
      const isRelativePath = key.indexOf('/') > -1;

      if (isRelativePath) {
        window.location.href = key;
      } else {
        this.transitionTo(key);
      }
    } else {
      this._super(...arguments);
    }
  }
});
