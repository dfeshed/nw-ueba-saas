import Ember from 'ember';

const {
  Route,
  getOwner
} = Ember;

export default Route.extend({
  model() {
    const applicationInstance = getOwner(this);
    const auth = applicationInstance.lookup('authenticator:oauth-authenticator');
    return auth.authenticate('local', 'changeMe');
  }
});
