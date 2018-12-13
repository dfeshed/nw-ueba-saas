/**
* @file Custom authenticator
* @description custom version of ember-simple-auth that invokes our apis for
* login, logout and restore session
* @public
*/

import { inject as service } from '@ember/service';

import fetch from 'component-lib/services/fetch';
import { run } from '@ember/runloop';
import { isEmpty } from '@ember/utils';
import RSVP from 'rsvp';
import { makeArray } from '@ember/array';
import OAuth2PasswordGrant from 'ember-simple-auth/authenticators/oauth2-password-grant';
import csrfToken from '../mixins/csrf-token';
import moment from 'moment';
import config from 'ember-get-config';

const { useMockServer, mockServerUrl } = config;

export default OAuth2PasswordGrant.extend(csrfToken, {

  serverTokenEndpoint: '/oauth/token',

  checkTokenEndpoint: '/oauth/check',

  clientId: 'nw_ui',

  session: service(),

  flashMessages: service(),

  i18n: service(),

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

    if (csrfKey) {
      localStorage.removeItem(csrfKey);

      fetch('/oauth/logout', {
        credentials: 'same-origin',
        method: 'POST',
        headers: {
          'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
        },
        body: {
          access_token: this.get('session.persistedAccessToken')
        }
      }).finally(() => {
        this.set('session.persistedAccessToken', null);
        this.get('session').invalidate();
      });
    }
  },

  authenticate(identification, password, scope = []) {
    return new RSVP.Promise((resolve, reject) => {
      const data = { 'grant_type': 'password', username: identification, password };
      const serverTokenEndpoint = useMockServer ? `${mockServerUrl}${this.get('serverTokenEndpoint')}` : this.get('serverTokenEndpoint');
      const scopesString = makeArray(scope).join(' ');
      if (!isEmpty(scopesString)) {
        data.scope = scopesString;
      }
      this.makeRequest(serverTokenEndpoint, data).then((response) => {
        run(() => {
          const { responseJSON } = response;
          const { headers } = response;
          const csrfKey = this.get('csrfLocalstorageKey');

          if (headers) {
            const csrf = headers.get('X-CSRF-TOKEN') || null;
            const idleSessionTimeout = (parseInt(headers.get('X-NW-Idle-Session-Timeout') || 10, 10)) * 60000;
            localStorage.setItem('rsa-x-idle-session-timeout', idleSessionTimeout);
            localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());

            if (csrf) {
              localStorage.setItem(csrfKey, csrf);
            }
          }

          const daysRemaining = responseJSON.expiryUserNotify;
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

          this.set('session.persistedAccessToken', responseJSON.access_token);
          resolve(responseJSON);
        });
      }, (xhr) => {
        run(null, reject, xhr.responseJSON || xhr.responseText);
      });
    });
  },

  restore(data) {
    const csrfKey = this.get('csrfLocalstorageKey');
    if (csrfKey) {
      return new RSVP.Promise((resolve, reject) => {
        fetch(this.get('checkTokenEndpoint'), { credentials: 'include', method: 'GET' }).then((response) => {
          // Http Status code 401/500 is 'successful'. See https://github.com/github/fetch#caveats
          if (!this._validate(data) || response.status >= 300) {
            reject();
          } else {
            response.text().then((token) => {
              this.set('session.persistedAccessToken', token);
              resolve(data);
            });
          }
        }).catch(reject);
      });
    }
  },

  _validate(data) {
    const csrfToken = localStorage.getItem(this.get('csrfLocalstorageKey'));
    return !isEmpty(data.access_token) && (useMockServer || !isEmpty(csrfToken));
  }
});
