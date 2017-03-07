/**
 * @file Login component
 * @description component responsible for establishing new session
 * @public
 */

import Ember from 'ember';
import layout from './template';
import computed, { readOnly, alias, notEmpty, equal } from 'ember-computed-decorators';

const {
  getOwner,
  Component,
  run,
  typeOf,
  inject: {
    service
  },
  Logger,
  isEmpty
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
  ajax: service(),
  appVersion: service(),
  request: service(),
  session: service(),

  classNames: ['rsa-login'],

  errorMessage: null,

  eulaContent: null,

  eulaKey: 'rsa::netWitness::eulaAccepted',

  layout,

  newPassword: null,

  newPasswordConfirm: null,

  password: null,

  tagName: 'centered',

  title: null,

  status: _STATUS.INIT,

  username: null,

  mustChangePassword: false,

  @computed('eulaKey')
  displayEula: {
    get(eulaKey) {
      return isEmpty(localStorage.getItem(eulaKey));
    },
    set(value, eulaKey) {
      localStorage.setItem(eulaKey, true);
      return value;
    }
  },

  @notEmpty('errorMessage')
  hasError: false,

  @equal('status', _STATUS.WAIT)
  isAuthenticating: false,

  @computed('username', 'password', 'status')
  isLoginDisabled: (uid, password, status) => {
    const uidFails = (typeOf(uid) !== 'string') || (uid.trim().length === 0);
    const pwFails = (typeOf(password) !== 'string') || (password.trim().length === 0);
    const waiting = status === _STATUS.WAIT;
    return (uidFails || pwFails || waiting);
  },

  @computed('password', 'newPassword', 'newPasswordConfirm', 'status')
  changePasswordDisabled: (password, newPassword, newPasswordConfirm, status) => {
    const pwFails = (typeOf(password) !== 'string') || (password.trim().length === 0);
    const newPwFails = (typeOf(newPassword) !== 'string') || (newPassword.trim().length === 0);
    const confirmPwFails = (typeOf(newPasswordConfirm) !== 'string') || (newPasswordConfirm.trim().length === 0);
    const waiting = status === _STATUS.WAIT;

    return (pwFails || newPwFails || confirmPwFails || waiting);
  },

  @readOnly
  @alias('appVersion.version')
  version: null,

  authenticate() {
    // Update status to that UI can indicate that a login is in progress.
    this.setProperties({
      status: _STATUS.WAIT,
      errorMessage: null
    });

    const session = this.get('session');

    if (session) {
      // Calls the custom sa-authenticator app/authenticators/sa-authenticator
      const config = getOwner(this).resolveRegistration('config:environment');
      const auth = config['ember-simple-auth'].authenticate;

      session.authenticate(auth, this.get('username'), this.get('password')).then(
        // Auth succeeded
        () => {
          this.updateLoginProperties(_STATUS.SUCCESS);

          const query = window.location.search;

          if (!isEmpty(query)) {
            window.location = window.location.search.substring(6);
          }
        },

        // Auth failed
        (message) => {
          if (message.user && message.user.mustChangePassword) {
            this.updateLoginProperties(_STATUS.INIT, null, true);
          } else {
            let errorMessage = 'login.genericError';
            const exception = message.error_description;

            if (exception) {
              if (exception.includes('locked')) {
                errorMessage = 'login.userLocked';
              } else if (exception.includes('disabled')) {
                errorMessage = 'login.userDisabled';
              } else if (exception.includes('expired')) {
                errorMessage = 'login.userExpired';
              }
            }
            this.updateLoginProperties(_STATUS.ERROR, errorMessage);

            this.$('.js-test-login-username-input').focus();
          }
        }
      );
    }
  },

  changePassword() {
    this.set('status', _STATUS.WAIT);

    if (this.get('newPassword') !== this.get('newPasswordConfirm')) {
      this.updateLoginProperties(_STATUS.INIT, 'login.passwordMismatch', true);
    } else if (this.get('newPassword') === this.get('password')) {
      this.updateLoginProperties(_STATUS.INIT, 'login.passwordNoChange', true);
    } else {
      this.set('status', _STATUS.SUCCESS);
      this.get('request').promiseRequest({
        method: 'updatePassword',
        modelName: 'passwords',
        query: {
          data: {
            currentPassword: this.get('password'),
            newPassword: this.get('newPassword')
          }
        }
      }).then(() => {
        this.updateLoginProperties(_STATUS.SUCCESS);
      }).catch(() => {
        this.updateLoginProperties(_STATUS.INIT, 'login.passwordChangeFailed', true);
      });
    }
  },

  updateLoginProperties(status, errorMessage = null, mustChangePassword = false) {
    this.setProperties({
      username: null,
      password: null,
      newPassword: null,
      newPasswordConfirm: null,
      status,
      errorMessage,
      mustChangePassword
    });
  },

  didInsertElement() {
    run.scheduleOnce('afterRender', () => {
      if (this.get('displayEula')) {
        const { requestEula } = getOwner(this).resolveRegistration('config:environment');

        if (requestEula) {
          this.get('ajax').request('/eula/rsa', {
            dataType: 'html'
          }).then((response) => {
            this.set('eulaContent', response);
          }).catch((error) => {
            Logger.error(error);
          });
        }
      } else {
        this.$('.js-test-login-username-input').focus();
      }
    });
  },

  actions: {
    acceptEula() {
      this.set('displayEula', false);
    },

    authenticate() {
      this.authenticate();
    },

    changePassword() {
      this.changePassword();
    }
  }
});
