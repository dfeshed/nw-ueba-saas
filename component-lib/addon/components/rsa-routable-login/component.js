/**
 * @file Login component
 * @description component responsible for establishing new session
 * @public
 */

import $ from 'jquery';
import Component from '@ember/component';
import Ember from 'ember';
import getOwner from 'ember-owner/get';
import { isEmpty, typeOf } from '@ember/utils';
import { run, later } from '@ember/runloop';
import { inject as service } from '@ember/service';
import layout from './template';
import computed, {
  readOnly,
  alias,
  notEmpty,
  equal
} from 'ember-computed-decorators';
import config from 'ember-get-config';
import { set } from '@ember/object';
import RSVP from 'rsvp';
import { warn } from '@ember/debug';
import { sanitizeHtml } from 'component-lib/utils/sanitize';
import { ieEdgeDetection } from 'component-lib/utils/browser-detection';

const { Promise } = RSVP;

const {
  testing
} = Ember;

const endpoint = function(path) {
  const { useMockServer, mockServerUrl } = config;
  return useMockServer ? `${mockServerUrl}${path}` : path;
};

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

const setPostAuthRedirect = () => {
  if (!testing && !isEmpty(window.location.search)) {
    const redirectionURL = window.location.search.substring(6);
    /* redirectionURL is constrained to start with '/' which implies internal redirection always and no external redirection.
    External redirections will never start with '/' */
    if (redirectionURL.startsWith('/')) {
      localStorage.setItem('rsa-post-auth-redirect', redirectionURL);
    } else {
      localStorage.setItem('rsa-post-auth-redirect', '');
    }
  }
};

export default Component.extend({
  ajax: service(),
  appVersion: service(),
  request: service(),
  session: service(),

  classNames: ['rsa-login'],

  errorMessage: null,

  eulaContent: null,
  eulaContentDelay: null,

  eulaKey: 'rsa::netWitness::eulaAccepted',

  displayPolicies: false,

  layout,

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

  securityBannerTitle: null,

  securityBannerText: null,

  displaySecurityBanner: null,

  isBrowserIeEdge: ieEdgeDetection(),

  userPkiEnabled: null,

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

  @computed('eulaContent', 'eulaContentDelay')
  eulaContentPending: {
    get(eulaContent, eulaContentDelay) {
      if (!eulaContentDelay) {
        return true;
      }
      return eulaContent === null;
    }
  },

  @computed('isBrowserIeEdge', 'displayEula')
  browserWarning: (isBrowserIeEdge, displayEula) => {
    return (!displayEula && isBrowserIeEdge);
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
      session.authenticate(auth, this.get('username'), this.get('password'), this.get('userPkiEnabled')).then(
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

            this.$('.js-test-login-username-input').focus();
          }
        }
      ).catch(() => {
        this.updateLoginProperties(_STATUS.ERROR, 'login.communicationError');
      });
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
      $.ajax({
        url: '/api/administration/security/user/updatePassword',
        method: 'POST',
        data: {
          'userName': this.get('username'),
          'currentPassword': this.get('password'),
          'newPassword': this.get('newPassword')
        }
      }).then(() => {
        this.updateLoginProperties(_STATUS.SUCCESS);
      }).fail(() => {
        this.updateLoginProperties(_STATUS.INIT, 'login.passwordChangeFailed', true);
      });
    }
  },

  fetchPasswordPolicy() {
    this.updateLoginProperties(_STATUS.INIT, null, true);

    if (config.adminServerAvailable) {
      this.get('ajax').request('/api/administration/security/password/policyMessages').then((response) => {
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
      if (this.get('displayEula')) {
        const { requestEula } = getOwner(this).resolveRegistration('config:environment');

        if (requestEula) {
          this.get('ajax').request('/eula/rsa', {
            dataType: 'html'
          }).then((response) => {
            const html = sanitizeHtml(response);
            this.set('eulaContent', html);

            return new Promise((resolve) => {
              later(() => {
                resolve();
              });
            }).then(() => {
              window.requestAnimationFrame(() => window.requestAnimationFrame(() => {
                if (this.get('isDestroying') || this.get('isDestroyed')) {
                  return;
                }
                set(this, 'eulaContentDelay', true);
              }));
            });

          }).catch((error) => {
            warn(error, { id: 'component-lib.components.rsa-routable-login.component' });
          });
        }
      }

      // Find out if the PKI Status is `on` or `off`!
      // Make a REST Call
      const pkiUrl = endpoint('/userpkistatus');
      const promisePki = this.get('ajax').request(pkiUrl, {
        dataType: 'html' // Capture the Response body!
      });

      // Get banner for Security Banner
      // Make REST call
      const eulaUrl = endpoint('/display/security/securitybanner/get');
      const promiseSecurityBanner = this.get('ajax').request(eulaUrl);

      // Wait for both Promise to return
      // Once both complete, resolve it
      Promise.all([promisePki, promiseSecurityBanner]).then((values) => {

        // Get and Set the Pki Status. In case of error as well consider PKi as
        // False because this is likely to be a Misconfigured NginX
        this.set('userPkiEnabled', values[0] === 'on');

        // Get the Security Banner Configuration
        const [config] = values[1].data;

        // Is Security Banner is supposed to be shown on UI?
        if (config.securityBannerEnabled) {

          // If Enabled, get the UI Text
          const bannerTitleHtml = sanitizeHtml(config.securityBannerTitle);
          const bannerTextHtml = sanitizeHtml(config.securityBannerText);

          // Set the Properties as needed
          this.setProperties({
            securityBannerTitle: bannerTitleHtml,
            securityBannerText: bannerTextHtml,
            displaySecurityBanner: true
          });

        } else {

          // If Not Enabled, we simply need to Put Focus on Username Input
          this.$('.js-test-login-username-input').focus();

          // If Pki is Enabled, we would like to auto Login
          // Let the handler figure it out, as we already know if pki is Enabled
          this.handlePkiEnabledLogin();
        }
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
      this.$('.js-test-login-username-input').hide();
      // We do not need user to input any Credential, So hide the input
      this.$('.js-test-login-password-input').hide();
      // Finally make the call to authenticate so that Certificate is sent to NginX
      this.authenticate();
    }
  },

  actions: {
    acceptEula() {
      this.set('displayEula', false);
    },

    acceptSecurityBanner() {
      this.set('displaySecurityBanner', false);
      this.handlePkiEnabledLogin();
    },

    authenticate() {
      this.authenticate();
    },

    changePassword() {
      this.changePassword();
    }
  }
});
