import Ember from 'ember';
import { authenticateSession } from 'sa/tests/helpers/ember-simple-auth';

const { Test } = Ember;

export default Test.registerAsyncHelper('authenticateSession', function(app) {
  authenticateSession(app, { 'authenticated': { 'authenticator': 'authenticator:sa-authenticator', 'access_token': 'success', 'username': 'admin', 'password': null } });
});
