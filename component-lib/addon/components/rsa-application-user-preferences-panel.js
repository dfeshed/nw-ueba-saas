import Ember from 'ember';
import layout from '../templates/components/rsa-application-user-preferences-panel';
import csrfToken from '../mixins/csrf-token';

const {
  getOwner,
  Component,
  inject: {
    service
  },
  run,
  Logger
} = Ember;

export default Component.extend(csrfToken, {

  layout,

  classNames: ['rsa-application-user-preferences-panel'],

  classNameBindings: ['isExpanded'],

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

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-user-preferences-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
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
      this.set('landingPage.selected', selection);
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
