/**
* @file Custom authenticator
* @description custom version of ember-simple-auth that invokes our apis for
* login, logout and restore session
* @public
*/

import Ember from 'ember';
import OAuth2PasswordGrant from 'ember-simple-auth/authenticators/oauth2-password-grant';
import csrfToken from '../mixins/csrf-token';
import oauthToken from '../mixins/oauth-token';

const {
  inject: {
    service
  },
  RSVP,
  isEmpty,
  run,
  merge,
  makeArray
} = Ember;

export default OAuth2PasswordGrant.extend(csrfToken, oauthToken, {

  serverTokenEndpoint: '/oauth/token',

  ajax: service(),

  /**
  * Responsible for restoring the session after a page reload, given the persisted data from the session.
  * Should return a Promise that will resolve when the session is restored. The return value of that Promise
  * will be automatically put in localStorage, overwriting previously persisted session data.
  * See Simple Auth API docs: http://ember-simple-auth.com/ember-simple-auth-api-docs.html#SimpleAuth-Authenticators-Base-restore
  * @param data The persisted session data from the last valid login.
  * @returns {Ember.RSVP.Promise} A Promise that resolves with the session data to be persisted in local storage.
  * @public
  */
  restore(data) {
    // We don't want to lose the persisted session data in localStorage, so we merge it on top of whatever other
    // info we need to fetch here.
    return new RSVP.Promise(function(resolve, reject) {
      if (!isEmpty(data.username)) {
        resolve(data);
      } else {
        reject();
      }
    });
  },

  /**
  * @function authenticate
  * @param credentials {Object} that holds username and password
  * @param credentials.identification {string} username of the user
  * @param credentials.password {string} password of the user
  * @public
  */
  authenticate(identification, password, scope = []) {
    let accessTokenKey = this.get('accessTokenKey');
    return new RSVP.Promise((resolve, reject) => {
      const data                = { client_id: 'nw_app', 'grant_type': 'password', username: identification.username, password: identification.password };
      const serverTokenEndpoint = this.get('serverTokenEndpoint');
      const scopesString = makeArray(scope).join(' ');
      if (!isEmpty(scopesString)) {
        data.scope = scopesString;
      }
      this.makeRequest(serverTokenEndpoint, data).then((response) => {
        run(() => {
          localStorage.setItem(accessTokenKey, response.access_token);
          document.cookie = `access_token=${response.access_token};path=/`;
          const expiresAt = this._absolutizeExpirationTime(response.expires_in);
          this._scheduleAccessTokenRefresh(response.expires_in, expiresAt, response.refresh_token);
          if (!isEmpty(expiresAt)) {
            response = merge(response, { 'expires_at': expiresAt });
          }
          resolve(response);
        });
      }, (xhr) => {
        run(null, reject, xhr.responseJSON || xhr.responseText);
      });
    });
  },


  /**
  * @function invalidate
  * @public
  */
  invalidate() {
    let accessTokenKey = this.get('accessTokenKey');
    let csrfKey = this.get('csrfLocalstorageKey');
    return this.get('ajax').raw('/api/user/logout', {
      type: 'POST',
      // do not wait forever
      timeout: 3000,
      // logout requires the CSRF token
      data: {
        '_csrf': localStorage.getItem(csrfKey)
      }
    }).then(function() {
      localStorage.removeItem(accessTokenKey);
      localStorage.removeItem(csrfKey);
    }).catch(function() {
      // Server down? Timed out? - still invalidate!
      localStorage.removeItem(accessTokenKey);
      localStorage.removeItem(csrfKey);
    });
  }

});
