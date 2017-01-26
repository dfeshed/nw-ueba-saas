import Ember from 'ember';
import config from 'dummy/config/environment';

const {
  Route,
  getOwner
} = Ember;

export default Route.extend({
  model() {

    // We do not want to force a login if we are running
    // local mocks (local node server)
    if (!config.mock) {
      const applicationInstance = getOwner(this);
      const auth = applicationInstance.lookup('authenticator:oauth-authenticator');
      return auth.authenticate('local', 'changeMe');
    }
  }
});
