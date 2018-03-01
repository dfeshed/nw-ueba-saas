/**
* @file Custom authenticator
* @description custom version of ember-simple-auth that invokes our apis for
* login, logout and restore session
* @public
*/

import { inject as service } from '@ember/service';

import $ from 'jquery';
import { run } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import RSVP from 'rsvp';
import { assign } from '@ember/polyfills';
import { makeArray } from '@ember/array';
import OAuth2PasswordGrant from 'ember-simple-auth/authenticators/oauth2-password-grant';
import csrfToken from '../mixins/csrf-token';
import oauthToken from '../mixins/oauth-token';
import moment from 'moment';
import config from 'ember-get-config';

const { useMockServer, mockServerUrl } = config;

export default OAuth2PasswordGrant.extend(csrfToken, oauthToken, {

  serverTokenEndpoint: '/oauth/token',

  clientId: 'nw_ui',

  session: service(),

  flashMessages: service(),

  i18n: service(),

  _addListeners: function() {
    const session = this.get('session');

    // when the session invalidates aka 'logout', clean up
    session.on('invalidationSucceeded', () => {
      // no longer need to observe changes
      session.get('session').removeObserver('content.authenticated', this, this._updateTokens);

      const accessTokenKey = this.get('accessTokenKey');
      const refreshTokenKey = this.get('refreshTokenKey');

      localStorage.removeItem(accessTokenKey);
      localStorage.removeItem(refreshTokenKey);
    });

    // make sure the tokens are up to date when refresh occurs
    session.get('session').addObserver('content.authenticated', this, this._updateTokens);

  }.on('init'),

  _updateTokens() {
    const { accessTokenKey, refreshTokenKey, session } =
      this.getProperties('accessTokenKey', 'refreshTokenKey', 'session');

    const authentication = session.get('session.content').authenticated;

    if (authentication) {
      localStorage.setItem(accessTokenKey, authentication.access_token);

      if (authentication.refresh_token) {
        localStorage.setItem(refreshTokenKey, authentication.refresh_token);
      }
    } else {
      session.invalidate();
    }
  },

  _scheduleAccessTokenRefresh(expiresIn, expiresAt) {
    const now = moment.now();
    if (isEmpty(expiresAt) && !isEmpty(expiresIn)) {
      expiresAt = moment.add(expiresIn, 'seconds').toDate();
    }
    const offset = (Math.floor(Math.random() * 5) + 5) * 1000;
    if (!isEmpty(expiresAt) && expiresAt > now - offset) {
      if (!isEmpty(this._tokenTimeout)) {
        run.cancel(this._tokenTimeout);
        delete this._tokenTimeout;
      }
      this._tokenTimeout = run.later(this, this._logoutAndInvalidate, expiresAt - now - offset);
    }
  },

  _logoutAndInvalidate() {
    const csrfKey = this.get('csrfLocalstorageKey');

    $.ajax({
      type: 'POST',
      url: '/oauth/logout',
      timeout: 15000,
      headers: {
        'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
      },
      data: {
        access_token: this.get('session').get('data.authenticated.access_token')
      }
    }).always(() => {
      localStorage.removeItem(csrfKey);
      this.get('session').invalidate();
    });
  },

  authenticate(identification, password, scope = []) {
    return new RSVP.Promise((resolve, reject) => {
      const data = { 'grant_type': 'password', username: identification, password };
      const serverTokenEndpoint = useMockServer ? `${mockServerUrl}${this.get('serverTokenEndpoint')}` : this.get('serverTokenEndpoint');
      const scopesString = makeArray(scope).join(' ');
      if (!isEmpty(scopesString)) {
        data.scope = scopesString;
      }
      this.makeRequest(serverTokenEndpoint, data).then((response, status, jqXHR) => {
        run(() => {
          const csrfKey = this.get('csrfLocalstorageKey');

          if (jqXHR) {
            const csrf = jqXHR.getResponseHeader('X-CSRF-TOKEN') || null;
            const idleSessionTimeout = (parseInt(jqXHR.getResponseHeader('X-NW-Idle-Session-Timeout') || 10, 10)) * 60000;
            localStorage.setItem('rsa-x-idle-session-timeout', idleSessionTimeout);
            localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());

            if (csrf) {
              localStorage.setItem(csrfKey, csrf);
            }
          }

          const daysRemaining = response.expiryUserNotify;
          let passwordChangeKey = 'login.changePasswordSoon';

          if (daysRemaining === 0) {
            passwordChangeKey = 'login.changePasswordToday';
          }

          if (daysRemaining > -1) {
            this.get('flashMessages').warning(this.get('i18n').t(passwordChangeKey, {
              daysRemaining
            }), {
              sticky: true
            });
          }

          const expiresAt = this._absolutizeExpirationTime(response.expires_in);
          this._scheduleAccessTokenRefresh(response.expires_in, expiresAt, response.refresh_token);
          if (!isEmpty(expiresAt)) {
            response = assign(response, { 'expires_at': expiresAt });
          }
          resolve(response);
        });
      }, (xhr) => {
        run(null, reject, xhr.responseJSON || xhr.responseText);
      });
    });
  },

  restore(data) {
    return new RSVP.Promise((resolve, reject) => {
      const now = (new Date()).getTime();
      const refreshAccessTokens = this.get('refreshAccessTokens');
      if (!isEmpty(data.expires_at) && data.expires_at < now) {
        if (refreshAccessTokens) {
          this._refreshAccessToken(data.expires_in, data.refresh_token).then(resolve, reject);
        } else {
          reject();
        }
      } else {
        if (!this._validate(data)) {
          reject();
        } else {
          this._scheduleAccessTokenRefresh(data.expires_in, data.expires_at, data.refresh_token);
          resolve(data);
        }
      }
    });
  },

  _validate(data) {
    const csrfToken = localStorage.getItem(this.get('csrfLocalstorageKey'));
    return !isEmpty(data.access_token) && (useMockServer || !isEmpty(csrfToken));
  }
});
