import Ember from 'ember';
import { authenticateSession, currentSession } from 'sa/tests/helpers/ember-simple-auth';

const { Test } = Ember;

export default Test.registerAsyncHelper('authenticateSession', function(app) {
  authenticateSession(app, { 'authenticator': 'authenticator:sa-authenticator', 'access_token': 'success', 'user': { 'id': 'local','name': 'Local Service','description': 'The local service administrator' }, 'password': null });
  const session = currentSession(app);
  session.set('isFullyAuthenticated', true);
});
