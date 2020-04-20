import Component from '@ember/component';
import getOwner from 'ember-owner/get';
import { isEmpty } from '@ember/utils';
import { later } from '@ember/runloop';
import layout from './template';
import computed from 'ember-computed-decorators';
import { set } from '@ember/object';
import RSVP from 'rsvp';
import { warn } from '@ember/debug';
import { sanitizeHtml } from 'component-lib/utils/sanitize';
import fetch from 'component-lib/utils/fetch';
import { windowProxy } from 'component-lib/utils/window-proxy';

const { Promise } = RSVP;

export default Component.extend({

  layout,

  classNames: ['rsa-login'],

  eulaContent: null,

  eulaContentDelay: null,

  eulaKey: 'rsa::netWitness::eulaAccepted',

  tagName: 'centered',

  title: null,

  securityBannerTitle: null,

  securityBannerText: null,

  displaySecurityBanner: null,

  showLoginPage: false,

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

  _redirectTo() {
    if (this.get('isSsoEnabled')) {
      // redirect to ADFS login page
      windowProxy.openInCurrentTab('/saml/login');
    } else {
      this.set('showLoginPage', true);
    }
  },

  init() {
    this._super(...arguments);
    if (this.get('displayEula')) {
      const { requestEula } = getOwner(this).resolveRegistration('config:environment');

      if (requestEula) {
        fetch('/eula/rsa', {
          dataType: 'html'
        }).then((fetched) => fetched.text()).then((response) => {
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
          warn(error, { id: 'component-lib.components.rsa-eula.component' });
        });
      }
    }

    fetch('/display/security/securitybanner/get').then((fetched) => fetched.json()).then((response) => {
      // Get the Security Banner Configuration
      const [config] = response.data;

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
        this.element.querySelector('.js-test-login-username-input').focus();
      }
    }).catch((error) => {
      warn(error, { id: 'component-lib.components.rsa-eula.component' });
    });

    if (!this.get('displayEula') && !this.get('displaySecurityBanner')) {
      this._redirectTo();
    }
  },

  actions: {
    acceptEula() {
      this.set('displayEula', false);
      if (!this.get('displaySecurityBanner')) {
        this._redirectTo();
      }
    },

    acceptSecurityBanner() {
      this.set('displaySecurityBanner', false);
      this._redirectTo();
    }
  }
})
;
