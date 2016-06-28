/**
* @file Login component
* @description component responsible for establishing new session
* @public
*/

import Ember from 'ember';
import layout from '../templates/components/rsa-routable-login';
const { getOwner } = Ember;

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

export default Ember.Component.extend({

  layout,

  classNames: ['rsa-grid', 'rsa-login'],

  title: null,

  /**
   * Username.
   * The user's inputted id from the login UI. Is set at run-time as user types.
   * @type String
   * @public
   */
  username: null,

  /**
   * User password.
   * The user's inputted pwd from the login UI. Is set at run-time as user types.
   * Disables browser autofill.
   * Password field binding does not support browser autofill
   * @type String
   * @public
   */
  password: Ember.computed({
    get() {
      return this.get('_password');
    },

    set(key, value) {
      this.set('_password', value);
      this.passwordDidChange();
      return value;
    }
  }),

  /**
   * Used disabling browser autofill.
   * Password field binding does not support browser autofill
   * @type Boolean
   * @private
   */
  passwordDidChange() {
    Ember.run.schedule('afterRender', () => {
      this.$('input:last').attr('type', 'password');
    });
  },

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
  * Indicates the user has started the password reset process.
  * @type Boolean
  * @default false
  * @public
  */
  willRequestPasswordReset: false,

  /**
  * Indicates the user has completed the password reset process.
  * @type Boolean
  * @default false
  * @public
  */
  didRequestPasswordReset: false,

  /**
  * Indicates the user has completed the password reset process.
  * @type Boolean
  * @default false
  * @public
  */
  hasError: Ember.computed.notEmpty('errorMessage'),

  /**
  * Only false when the 'username' and 'password' properties are non-empty strings with some non-space character.
  * Used for enabling/disabling the login button in the UI.
  * @type Boolean
  * @public
  */
  isLoginDisabled: Ember.computed('username', 'password', 'status', function() {
    let uid = this.get('username'),
    password = this.get('password'),
    uidFails = (Ember.typeOf(uid) !== 'string') || (uid.trim().length === 0),
    pwFails = (Ember.typeOf(password) !== 'string') || (password.trim().length === 0);

    return (uidFails || pwFails || (this.get('status') === _STATUS.WAIT));
  }),

  isResetDisabled: Ember.computed('username', 'status', function() {
    let uid = this.get('username'),
    uidFails = (Ember.typeOf(uid) !== 'string') || (uid.trim().length === 0);

    return (uidFails || (this.get('status') === _STATUS.WAIT));
  }),

  actions: {

    /**
    * Begins password reset process
    * @public
    */
    initiatePasswordReset() {
      this.set('willRequestPasswordReset', true);
    },

    /**
    * Make reset request
    * @public
    */
    requestPasswordReset() {
      this.set('didRequestPasswordReset', true);
    },

    /**
    * Resets login process defaults, returns to login page
    * @public
    */
    resetComplete() {
      this.set('username', null);
      this.set('password', null);
      this.set('willRequestPasswordReset', false);
      this.set('didRequestPasswordReset', false);
    },

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
        let config = getOwner(this).resolveRegistration('config:environment'),
            auth = config['ember-simple-auth'].authenticate;

        session.authenticate(auth, credentials).then(
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
