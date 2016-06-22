/**
* @file Custom authenticator
* @description custom version of ember-simple-auth that invokes our apis for
* login, logout and restore session
* @public
*/

import Ember from 'ember';
import {raw} from 'ic-ajax';
import Base from 'ember-simple-auth/authenticators/base';
import csrfToken from '../mixins/csrf-token';

export default Base.extend(csrfToken, {
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
    return new Ember.RSVP.Promise(function(resolve, reject) {
      if (!Ember.isEmpty(data.username)) {
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
  authenticate(credentials) {
    let csrfKey = this.get('csrfLocalstorageKey');
    return raw({
      url: '/api/user/login',
      type: 'POST',
      data: credentials
    }).then(function(result) {
      let csrf = result.jqXHR.getResponseHeader('X-CSRF-TOKEN') || null;
      localStorage.setItem(csrfKey, csrf);
      // Promise must return a response so that it wil be cached in session.content.secure, which
      // can later be accessed by other code to read the current user's login & authorizations.
      return result && result.response;
    });
  },

  /**
  * @function invalidate
  * @public
  */
  invalidate() {
    let csrfKey = this.get('csrfLocalstorageKey');
    return raw({
      type: 'POST',
      url: '/api/user/logout',
      // do not wait forever
      timeout: 3000,
      // logout requires the CSRF token
      data: {
        '_csrf': localStorage.getItem(csrfKey)
      }
    }).then(function() {
      localStorage.removeItem(csrfKey);
    }).catch(function() {
      // Server down? Timed out? - still invalidate!
      localStorage.removeItem(csrfKey);
    });
  }

});
