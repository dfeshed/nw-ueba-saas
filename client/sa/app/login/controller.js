/**
* @file Login controller
* @description controller responsible for establishing new session
* @public
*/

import Ember from 'ember';
import config from '../config/environment';
/**
 * Enumeration of authentication status.
 * @private
 * @type {String{}}
 */
const _STATUS = {
  INIT: 'init',
  WAIT: 'wait',
  ERROR: 'error',
  SUCCESS: 'success'
};

export default Ember.Controller.extend({
  /**
   * Username.
   * The user's inputted id from the login UI. Is set at run-time as user types.
   * @type String
   * @public
   */
  username: '',

  /**
   * User password.
   * The user's inputted pwd from the login UI. Is set at run-time as user types.
   * @type String
   * @public
   */
  password: '',

  /**
   * Reason why the last authentication attempt failed.  Is set dynamically at run-time by
   * the 'authenticate' action. Is displayed in the login template. Is resets back to null after a successful attempt.
   * @type String
   * @default null
   * @public
   */
  errorMessage: null,

  /**
   * Indicates the status of the login request. Either: 'init', 'wait', 'err' or 'success'.
   * @type String
   * @default 'init'
   * @public
   */
  status: _STATUS.INIT,

  /**
   * Only false when the 'uid' property is a non-empty string with some non-space character.
   * Used for enabling/disabling the login button in the UI.
   * @type Boolean
   * @public
   */
  isLoginDisabled: function() {
    let uid = this.get('username');
    return (typeof uid !== 'string') || !uid.trim().length || (this.get('status') === _STATUS.WAIT);
  }.property('username', 'status'),

  actions: {
    /**
     * Establishes session when users logs in.
     * Updates the properties 'status' and 'errorMessage' accordingly, so that UI can
     * notify user of progress.
     * @listens login form submit action
     * @public
     */
    authenticate() {

      // Update status to that UI can indicate that a login is in progress.
      this.set('status', _STATUS.WAIT);

      let me = this,
        credentials = this.getProperties('username', 'password'),
        session = this.get('session');

      if (session) {
        // Calls the custom sa-authenticator app/authenticators/sa-authenticator
        session.authenticate(config['ember-simple-auth'].authenticate, credentials).then(

          // Auth succeeded
          function() {
            me.set('errorMessage', null);
            me.set('status', _STATUS.SUCCESS);
          },

          // Auth failed
          function(message) {
            let errorMessage = 'login.genericError';

            let exception = message.jqXHR.getResponseHeader('x-authentication-exception');
            if (exception) {
              if (exception.indexOf('BadCredentials') !== -1) {
                errorMessage = 'login.badCredentials';
              } else if (exception.indexOf('Locked') !== -1) {
                errorMessage = 'login.userLocked';
              } else if (exception.indexOf('Disabled') !== -1) {
                errorMessage = 'login.userDisabled';
              } else if (exception.indexOf('AuthenticationService') !== -1) {
                errorMessage = 'login.authServerNotFound';
              }
            }

            me.set('errorMessage', errorMessage);
            me.set('status', _STATUS.ERROR);
            Ember.Logger.log('Authentication error:', message);
          }
        );
      }
    }
  }
});
