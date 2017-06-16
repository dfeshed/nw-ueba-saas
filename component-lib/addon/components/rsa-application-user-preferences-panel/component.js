import Ember from 'ember';
import Component from 'ember-component';
import { isEmpty } from 'ember-utils';
import getOwner from 'ember-owner/get';
import layout from './template';
import run from 'ember-runloop';
import service from 'ember-service/inject';
import { alias, readOnly } from 'ember-computed-decorators';
import csrfToken from '../../mixins/csrf-token';
import config from 'ember-get-config';

const {
  Logger
} = Ember;

export default Component.extend(csrfToken, {

  layout,

  classNames: ['rsa-application-user-preferences-panel'],

  classNameBindings: ['isExpanded'],

  appVersion: service(),
  eventBus: service(),
  dateFormat: service(),
  landingPage: service(),
  layoutService: service('layout'),
  moment: service(),
  request: service(),
  timeFormat: service(),
  timezone: service(),

  isExpanded: false,
  locales: ['en-us', 'ja'],

  @readOnly @alias('appVersion.version') version: null,

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-user-preferences-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
    });

    run.schedule('afterRender', () => {
      if (isEmpty(this.get('timezone.selected'))) {
        this.set('timezone.selected', this.get('timezone.options').findBy('zoneId', config.timezoneDefault));
      }

      if (isEmpty(this.get('dateFormat.selected'))) {
        this.set('dateFormat.selected', this.get('dateFormat.options').findBy('key', config.dateFormatDefault));
      }

      if (isEmpty(this.get('timeFormat.selected'))) {
        this.set('timeFormat.selected', this.get('timeFormat.options').findBy('key', config.timeFormatDefault));
      }

      if (isEmpty(this.get('landingPage.selected'))) {
        this.set('landingPage.selected', this.get('landingPage.options').findBy('key', config.landingPageDefault));
      }
    });
  },

  didInsertElement() {
    run.schedule('afterRender', this, function() {
      if (this.get('i18n.locale')) {
        if (localStorage.getItem('rsa-i18n-default-locale')) {
          this.set('i18n.locale', localStorage.getItem('rsa-i18n-default-locale'));
        } else {
          const config = getOwner(this).resolveRegistration('config:environment');

          this.set('i18n.locale', config.i18n.defaultLocale);
          localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
        }
      }
    });
  },

  actions: {
    logout() {
      this.logout();
    },
    setLocale(selection) {
      this.set('i18n.locale', selection);
      this.get('request').promiseRequest({
        method: 'setPreference',
        modelName: 'preferences',
        query: {
          data: {
            userLocale: selection.replace(/en-us/, 'en_US')
          }
        }
      }).then(() => {
        localStorage.setItem('rsa-i18n-default-locale', selection);
      }).catch(() => {
        Logger.error('Error updating locale');
      });
    },

    setDefaultLandingPage(selection) {
      this.get('landingPage').setDefaultLandingPage(selection);
    },

    setTimezone(selection) {
      this.set('timezone.selected', selection);
    },

    setDateFormat(selection) {
      this.set('dateFormat.selected', selection);
    },

    setTimeFormat(selection) {
      this.set('timeFormat.selected', selection);
    },

    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    }
  }
});
