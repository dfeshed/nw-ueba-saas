/**
 * @file Login component
 * @description component responsible for establishing new session
 * @public
 */

import Ember from 'ember';
import layout from './template';


const {
  getOwner,
  Component,
  computed,
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
  appVersion: service(),
  session: service(),

  ajax: service(),

  classNames: ['rsa-login'],

  errorMessage: null,

  eulaContent: null,

  eulaKey: 'rsa::netWitness::eulaAccepted',

  layout,

  password: null,

  tagName: 'centered',

  title: null,

  status: _STATUS.INIT,

  username: null,

  displayEula: computed({
    get() {
      return isEmpty(localStorage.getItem(this.get('eulaKey')));
    },
    set(key, value) {
      localStorage.setItem(this.get('eulaKey'), true);
      return value;
    }
  }),

  isLoginDisabled: computed('username', 'password', 'status', function() {
    const uid = this.get('username');
    const password = this.get('password');
    const uidFails = (typeOf(uid) !== 'string') || (uid.trim().length === 0);
    const pwFails = (typeOf(password) !== 'string') || (password.trim().length === 0);

    return (uidFails || pwFails || (this.get('status') === _STATUS.WAIT));
  }),

  version: computed.readOnly('appVersion.version'),

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
          this.setProperties({
            status: _STATUS.SUCCESS,
            errorMessage: null
          });
        },

        // Auth failed
        (message) => {
          let errorMessage = 'login.genericError';
          const exception = message.error_description;

          if (exception) {
            if (exception.includes('Bad credentials')) {
              errorMessage = 'login.badCredentials';
            } else if (exception.includes('locked')) {
              errorMessage = 'login.userLocked';
            } else if (exception.includes('disabled')) {
              errorMessage = 'login.userDisabled';
            } else if (exception.includes('expired')) {
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
    }
  }
});
