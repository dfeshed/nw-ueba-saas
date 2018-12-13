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

const { Promise } = RSVP;

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
      session.authenticate(auth, this.get('username'), this.get('password')).then(
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

      this.get('ajax').request('/display/security/securitybanner/get').then((response) => {
        const [config] = response.data;

        if (config.securityBannerEnabled) {
          const bannerTitleHtml = sanitizeHtml(config.securityBannerTitle);
          const bannerTextHtml = sanitizeHtml(config.securityBannerText);
          this.setProperties({
            securityBannerTitle: bannerTitleHtml,
            securityBannerText: bannerTextHtml,
            displaySecurityBanner: true
          });
        } else {
          this.$('.js-test-login-username-input').focus();
        }
      }).catch((error) => {
        warn(error, { id: 'component-lib.components.rsa-routable-login.component' });
      });
    });
  },

  actions: {
    acceptEula() {
      this.set('displayEula', false);
    },

    acceptSecurityBanner() {
      this.set('displaySecurityBanner', false);
    },

    authenticate() {
      this.authenticate();
    },

    changePassword() {
      this.changePassword();
    }
  }
});
