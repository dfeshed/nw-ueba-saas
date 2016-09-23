/**
* @file Login component
* @description component responsible for establishing new session
* @public
*/

import Ember from 'ember';
import layout from '../templates/components/rsa-routable-login';

const {
  getOwner,
  Component,
  computed,
  run,
  typeOf
} = Ember;

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

export default Component.extend({

  layout,

  tagName: 'centered',

  classNames: ['rsa-login'],

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
  password: computed({
    get() {
      return this.get('_password');
    },

    set(key, value) {
      this.set('_password', value);
      this.passwordDidChange();
      return value;
    }
  }),

  didInsertElement() {
    run.schedule('afterRender', () => {
      this.$('.js-test-login-username-input').focus();
    });
  },

  /**
   * Used disabling browser autofill.
   * Password field binding does not support browser autofill
   * @type Boolean
   * @private
   */
  passwordDidChange() {
    run.schedule('afterRender', () => {
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
  hasError: computed.notEmpty('errorMessage'),

  /**
  * Only false when the 'username' and 'password' properties are non-empty strings with some non-space character.
  * Used for enabling/disabling the login button in the UI.
  * @type Boolean
  * @public
  */
  isLoginDisabled: computed('username', 'password', 'status', function() {
    let uid = this.get('username');
    let password = this.get('password');
    let uidFails = (typeOf(uid) !== 'string') || (uid.trim().length === 0);
    let pwFails = (typeOf(password) !== 'string') || (password.trim().length === 0);

    return (uidFails || pwFails || (this.get('status') === _STATUS.WAIT));
  }),

  isResetDisabled: computed('username', 'status', function() {
    let uid = this.get('username');
    let uidFails = (typeOf(uid) !== 'string') || (uid.trim().length === 0);

    return (uidFails || (this.get('status') === _STATUS.WAIT));
  }),

  actions: {

    /**
    * Begins password reset process
    * @public
    */
    initiatePasswordReset() {
      this.setProperties({
        status: null,
        username: null,
        password: null,
        errorMessage: null,
        willRequestPasswordReset: true
      });

      run.schedule('afterRender', () => {
        this.$('.js-test-lost-password-username-input').focus();
      });
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
      this.setProperties({
        status: null,
        username: null,
        password: null,
        errorMessage: null,
        willRequestPasswordReset: false,
        didRequestPasswordReset: false
      });

      run.schedule('afterRender', () => {
        this.$('.js-test-login-username-input').focus();
      });
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
      this.setProperties({
        status: _STATUS.WAIT,
        errorMessage: null
      });

      let session = this.get('session');

      if (session) {
        // Calls the custom sa-authenticator app/authenticators/sa-authenticator
        let config = getOwner(this).resolveRegistration('config:environment');
        let auth = config['ember-simple-auth'].authenticate;

        session.authenticate(auth, this.get('username'), this.get('password')).then(
          // Auth succeeded
          () => {
            this.setProperties({
              status: _STATUS.SUCCESS,
              errorMessage: null
            });
          },

          // Auth failed
          (message) => {
            let errorMessage = 'login.genericError';
            let exception = message.error_description;

            if (exception) {
              if (exception.indexOf('Bad credentials') !== -1) {
                errorMessage = 'login.badCredentials';
              } else if (exception.indexOf('locked') !== -1) {
                errorMessage = 'login.userLocked';
              } else if (exception.indexOf('disabled') !== -1) {
                errorMessage = 'login.userDisabled';
              } else if (exception.indexOf('expired') !== -1) {
                errorMessage = 'login.userDisabled';
              }
            }

            this.setProperties({
              status: _STATUS.ERROR,
              username: null,
              password: null,
              errorMessage
            });

            this.$('.js-test-login-username-input').focus();
          }
        );
      }
    }
  }
});
