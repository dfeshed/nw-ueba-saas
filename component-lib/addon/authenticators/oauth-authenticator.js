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
  }
} = Ember;

export default OAuth2PasswordGrant.extend(csrfToken, oauthToken, {

  serverTokenEndpoint: '/oauth/token',

  clientId: 'nw_ui',

  refreshAccessTokens: false,

  ajax: service(),

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
      session.invalidate();
    }
  }
});
