/**
 * @file Login component
 * @description component responsible for establishing new session
 * @public
 */

import Component from '@ember/component';
import Ember from 'ember';
import getOwner from 'ember-owner/get';
import { isEmpty, typeOf } from '@ember/utils';
import { run } from '@ember/runloop';
import { inject as service } from '@ember/service';
import layout from './template';
import computed, {
  readOnly,
  alias,
  notEmpty,
  equal
} from 'ember-computed-decorators';
import config from 'ember-get-config';
import { warn } from '@ember/debug';
import fetch from 'component-lib/utils/fetch';

const {
  testing
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

const permittedURLList = ['/respond', '/investigate', '/unified', '/reporting', '/live', '/configure', '/alerting', '/admin', '/profile'];

const setPostAuthRedirect = () => {
  if (!testing && !isEmpty(window.location.search)) {
    const redirectionURL = window.location.search.substring(6);
    /* redirectionURL is constrained to start with an approved list of internal paths */
    const isPresentInPermittedURLList = permittedURLList.some((subMatch) => subMatch.includes(redirectionURL.substring(0, 5)));
    if (isPresentInPermittedURLList && !(redirectionURL.includes('.') || redirectionURL.toLowerCase().includes('%2e'))) {
      localStorage.setItem('rsa-post-auth-redirect', redirectionURL);
    } else {
      localStorage.setItem('rsa-post-auth-redirect', '');
    }
  }
};

export default Component.extend({

  layout,

  appVersion: service(),

  request: service(),

  session: service(),

  classNames: ['rsa-login'],

  errorMessage: null,

  displayPolicies: false,

  newPassword: null,

  newPasswordConfirm: null,

  password: null,

  tagName: 'centered',

  title: null,

  status: _STATUS.INIT,

  username: null,

  mustChangePassword: false,

  passwordPolicyMinChars: null,

  passwordPolicyMinNumericChars: null,

  passwordPolicyMinUpperChars: null,

  passwordPolicyMinLowerChars: null,

  passwordPolicyMinNonLatinChars: null,

  passwordPolicyMinSpecialChars: null,

  passwordPolicyCannotIncludeId: null,

  userPkiEnabled: null,

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
    @alias('appVersion.marketingVersion')
  marketingVersion: null,

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

      localStorage.removeItem('rsa-x-csrf-token');
      // Authenticate based on Credential and Pki Status
      const grantType = this.get('userPkiEnabled') ? 'pki' : 'password';
      session.authenticate(auth, this.get('username'), this.get('password'), grantType).then(
        // Auth succeeded
        () => {
          this.updateLoginProperties(_STATUS.SUCCESS);
          setPostAuthRedirect();
        },

        // Auth failed
        (message) => {
          const exception = message.error_description;

          if (exception.includes('expired') || exception.includes('Password does not meet requirements')) {
            this.fetchPasswordPolicy();
          } else {
            let errorMessage = 'login.genericError';

            if (exception) {
              if (exception.includes('locked')) {
                errorMessage = 'login.userLocked';
              } else if (exception.includes('disabled')) {
                errorMessage = 'login.userDisabled';
              }
            }
            this.updateLoginProperties(_STATUS.ERROR, errorMessage);
            this.element.querySelector('.js-test-login-username-input').focus();
          }
        }
      ).catch(() => {
        this.updateLoginProperties(_STATUS.ERROR, 'login.communicationError');
      });
    }
  },

  changePassword() {
    this.set('status', _STATUS.WAIT);
    this.set('data', new FormData());
    this.get('data').set('userName', this.get('username'));
    this.get('data').set('currentPassword', this.get('password'));
    this.get('data').set('newPassword', this.get('newPassword'));

    if (this.get('newPassword') !== this.get('newPasswordConfirm')) {
      this.updateLoginProperties(_STATUS.INIT, 'login.passwordMismatch', true);
    } else if (this.get('newPassword') === this.get('password')) {
      this.updateLoginProperties(_STATUS.INIT, 'login.passwordNoChange', true);
    } else {
      this.set('status', _STATUS.SUCCESS);
      fetch('/api/administration/security/user/updatePassword', {
        method: 'POST',
        body: this.get('data')
      }).then((response) => {
        if (response.status === 200) {
          this.updateLoginProperties(_STATUS.SUCCESS);
        } else {
          this.updateLoginProperties(_STATUS.INIT, 'login.passwordChangeFailed', true);
        }
      }).catch(() => {
        this.updateLoginProperties(_STATUS.INIT, 'login.passwordChangeFailed', true);
      });
    }
  },

  fetchPasswordPolicy() {
    this.updateLoginProperties(_STATUS.INIT, null, true);
    if (config.adminServerAvailable) {
      fetch('/api/administration/security/password/policyMessages').then((response) => {
        this.setProperties({
          displayPolicies: true,
          passwordPolicyMinChars: response.passwordPolicyMinChars,
          passwordPolicyMinNumericChars: response.passwordPolicyMinNumericChars,
          passwordPolicyMinUpperChars: response.passwordPolicyMinUpperChars,
          passwordPolicyMinLowerChars: response.passwordPolicyMinLowerChars,
          passwordPolicyMinNonLatinChars: response.passwordPolicyMinNonLatinChars,
          passwordPolicyMinSpecialChars: response.passwordPolicyMinSpecialChars,
          passwordPolicyCannotIncludeId: response.passwordPolicyCannotIncludeId
        });
      }).catch(() => {
        this.get('flashMessages').warning(this.get('i18n').t('passwordPolicy.passwordPolicyRequestError'), {
          iconName: 'report-problem-circle'
        });
      });
    }
  },

  updateLoginProperties(status, errorMessage = null, mustChangePassword = false) {
    this.setProperties({
      username: mustChangePassword ? this.get('username') : null,
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

      // Find out if the PKI Status is `on` or `off`!
      // Make a REST Call
      fetch('/userpkistatus', {
        dataType: 'html' // Capture the Response body!
      }).then((fetched) => fetched.json()).then((response) => {

        // Get and Set the Pki Status. In case of error as well consider PKi as
        // False because this is likely to be a Misconfigured NginX
        this.set('userPkiEnabled', response === 'on');

        // If Pki is Enabled, we would like to auto Login
        // Let the handler figure it out, as we already know if pki is Enabled
        this.handlePkiEnabledLogin();
      }).catch((error) => {
        // Highlight UI
        warn(error, { id: 'component-lib.components.rsa-routable-login.component' });
      });
    });
  },

  /**
   * Function will make the XHR call to generate OAuth Token automatically when Pki is Enabled. The UI flow is simple,
   *
   * First, we make a Call to url "/userpkistatus", if the Response is ON, then we assume that NginX is configured for
   * Two Way SSL Handshake between Browser and NginX. This would mean that this SSL Session has a Client Certificate
   * negotiated between Browser and NginX which will be used for User Authentication instead of Credentials. When we
   * get the response of "/userpkistatus" as 'on', we set the variable {@code userPkiEnabled} as 'true'.
   *
   * After we get the Pki Status, we handle the Security Banner if Enabled. If Enabled, we show the banner
   * and call this function on Accepting the Banner. If Disabled, we call this function instead.
   *
   * This function, will check is the variable {@code userPkiEnabled} is set to 'true'. If set, we hide the Credential
   * inputs and the set the Value of 'username' and 'password' as "pki".
   *
   * Finally, we call {@code authenticate} function to kick-in the call to Handle generation of Access Token!
   *
   * @see {@code oauth-authenticator.js#authenticate} for more details
   */
  handlePkiEnabledLogin() {
    // See if need to authenticate automatically in case PKI is Enabled on Server!
    if (this.get('userPkiEnabled')) {
      // Set any Dummy value for Username so that Any Empty Checks are passed through
      this.set('username', 'pki');
      // Set any Dummy value for Password so that Any Empty Checks are passed through
      this.set('password', 'pki');
      // We do not need user to input any Credential, So hide the input
      this.element.querySelector('.js-test-login-username-input').style.display = 'none';
      this.element.querySelector('.js-test-login-password-input').style.display = 'none';
      // Finally make the call to authenticate so that Certificate is sent to NginX
      this.authenticate();
    }
  },

  actions: {
    authenticate() {
      this.authenticate();
    },

    changePassword() {
      this.changePassword();
    }
  }
});
