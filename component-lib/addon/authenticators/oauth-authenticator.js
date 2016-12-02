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
import moment from 'moment';

const {
  inject: {
    service
  },
  $,
  run,
  isEmpty
} = Ember;

export default OAuth2PasswordGrant.extend(csrfToken, oauthToken, {

  serverTokenEndpoint: '/oauth/token',

  clientId: 'nw_ui',

  session: service(),

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
      session.get('session').set('isFullyAuthenticated', false);
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
    $.ajax({
      type: 'POST',
      url: '/oauth/logout',
      timeout: 2000,
      data: {
        access_token: this.get('session').get('data.authenticated.access_token')
      }
    }).always(()=>{
      this.get('session').invalidate();
    });
  }
});
