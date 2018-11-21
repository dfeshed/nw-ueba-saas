import teardownSockets from './teardown-sockets';
import { fillIn, click, getContext, settled } from '@ember/test-helpers';
import Authenticator from 'component-lib/authenticators/oauth-authenticator';
import { Promise } from 'rsvp';
import { get } from '@ember/object';
import { localStorageClear } from '../helpers/wait-for';

const SESSION_SERVICE_KEY = 'service:session';
const invalidateSession = function() {
  const { owner } = getContext();
  const session = owner.lookup(SESSION_SERVICE_KEY);
  const isAuthenticated = get(session, 'isAuthenticated');
  return Promise.resolve().then(() => {
    if (isAuthenticated) {
      return session.invalidate();
    }
  }).then(() => settled());
};

const patchTokenRefresh = () => {
  Authenticator.reopen({
    _scheduleAccessTokenRefresh() {
    }
  });
};

export function setupLoginTest(hooks) {
  hooks.beforeEach(function() {
    patchTokenRefresh();
    invalidateSession();
    localStorage.removeItem('rsa-oauth2-jwt-access-token');
    localStorage.removeItem('rsa-post-auth-redirect');
    return localStorageClear().then(() => {
      localStorage.setItem('rsa::netWitness::eulaAccepted', true);
    });
  });

  hooks.afterEach(function() {
    teardownSockets.apply(this);
    return localStorageClear();
  });

}

export async function login() {
  return settled().then(async() => {
    await fillIn('[test-id=loginUsername] input', 'admin');
    await fillIn('[test-id=loginPassword] input', 'netwitness');
    return click('[test-id=loginButton] button');
  });
}
